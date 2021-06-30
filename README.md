## infosec

### 任务概述

#### 目标

在约定部署环境及应用环境下，增强一个Java EE Web应用系统（简称InfoSec）的整体安全性。

### 技术栈

Java+Springboot+Mybatis+Security+Vue.js+Mysql

#### 需求概述

##### 功能需求

* 数据上，一个数据库表user存储系统用户信息
* 业务上，支持获取系统用户列表以及查看用户详细信息
* 用户可以通过用户名密码的方式登录系统

##### 安全需求

* 传输层安全通信
* 未登录系统的用户仅能操作index主页面以及login登录页面
* 只有拥有管理员权限的用户才能访问用户列表信息，普通用户仅能访问自身详细信息
* 系统支持数字证书的方式进行用户认证，从而替代用户名/密码机制
* 数据库系统具有一定的安全性配置，如防止敏感信息泄露等
* 服务器具有一定安全性配置，如防止恶意ping等
* 内网公网均可访问系统

#### 概要设计

##### 总体设计

整个系统做成前后端分离的项目，前端对外开放登录页面，用户详细信息展示以及用户列表信息展示页面；后端对外开放登陆接口，用户详细信息查询接口以及用户列表信息查询接口。

前端采用Vue.js，用ant-design组件库搭建，利用nginx部署；后端利用Springboot框架搭建，用户认证，权限认证等功能通过security框架实现，最后继承jar包，部署在8023端口。

##### 模块划分

<img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/a033c75d49d54fdc85384cc007bcda1binfosec.png" width="600px" />

###### config

* Crosconfig：解决跨域问题
* SecurityConfiguration：SpringSecurity核心配置类，配置security基本信息，包括路径权限，异常处理器等
* TomcatHttpConfig：配置https

###### model

* User：结构化user数据
* SecurityMetadata：结构化路径权限数据

###### dao

* UserMapper：集成mybatis操作mysql数据库，获取用户列表以及单个用户信息

###### controller

* UserController：对外开放登录，注销，获取用户详细信息以及用户列表信息的接口

###### service

* MySecurityMetadataSource：获取请求路径所需要权限
* MyDecisionManager：判断当前用户权限是否满足请求路径所需权限
* UserDetailServiceImpl：通过dao层获取用户详细信息

###### utils

* Result：封装返回结果，包括返回数据以及状态码

###### handler

* AuthFilter：权限拦截器
* MyDeniedHandler：权限拦截异常处理器
* MyEntryPoint：认证异常处理器

##### 用户界面设计

###### index

<img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/f0c96f9dbc0544b0a0d7ae480c38c184image-20210621111355329.png" width="600px" />

###### login

<img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/73b657e888534bc08e4dff26edc24c60image-20210621111423884.png" width="600px" />

###### userList

<img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/e6fb7ee6ad0941fabd0138c083066075image-20210621111551379.png" width="600px" />

###### userDetail

<img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/da5258e1edbc49289ebe80c660baded6image-20210621111629639.png" width="600px" />

##### 数据库设计

###### user

``` mysql
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名',
  `showname` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示名',
  `password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码-SHA1哈希值',
  `question` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提问',
  `answer` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回答',
  `rolecode` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户类型{ROLE_ACAMGR|ROLE_TCH:|ROLE_STD}',
  `description` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `lastlogintime` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用，“0-否”，“1-是，启用” 默认：1',
  `accountNonExpired` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否过期，“0-否”，“1-是，未过期” 默认：1',
  `credentialsNonExpired` tinyint(1) NOT NULL DEFAULT 1 COMMENT '密码是否失效，“0-否”，“1-是，未失效” 默认：1',
  `accountNonLocked` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否被锁定-“0-否”，“1-是，未被锁定” 默认：1',
  PRIMARY KEY (`id`) USING BTREE
) 
```

#### 需求实现

* 数据上，一个数据库表user存储系统用户信息

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/4a4990f9449848fea934dd40d6848b9cimage-20210621143612292.png" width="600px" />

* 业务上，支持获取系统用户列表以及查看用户详细信息

  在controller层开放获取用户列表以及查看用户详细信息的接口，通过mybatis的orm技术连接到数据库，dao层配置相关查询语句。

  ``` java
  public User getUser(@Param(value="id")String id);
  public User getUserbyName(@Param(value="name")String name);
  ```

  ```xml
  <select id="listUsers" resultMap="userMap">
  	select * from user
  </select>
  <select id="getUser" resultMap="userMap" parameterType="java.lang.String">
  	select * from user where id=#{id}
  </select>
  <select id="getUserbyName" resultMap="userMap"  parameterType="java.lang.String">
  	select * from user where username=#{name}
  </select>
  ```

* 用户可以通过用户名密码的方式登录系统

  通过security的authenticationManager统一管理用户登录信息，通过SecurityContextHolder设置上下文信息，绑定连接session id，从而将登录信息与连接匹配起来。

  ``` java
  @RequestMapping(value = "/login", method = RequestMethod.GET)
      public Result login(@RequestParam("username") String username, @RequestParam("password") String password) {
          Authentication token = new UsernamePasswordAuthenticationToken(username, password);
          Authentication authentication = authenticationManager.authenticate(token);
          SecurityContextHolder.getContext().setAuthentication(authentication);
          HashMap res = new HashMap();
          res.put("token", token);
          return Result.succ(token);
      }
  ```

* 传输层安全通信

  通过配置ssl证书使得网站支持https，具体实现过程如下：

  1. 生成根机构证书rootCA.key rootCA.crt

     ``` shell
     openssl req -x509 -sha256 -days 3650 -newkey rsa:4096 -keyout rootCA.key -out rootCA.crt
     ```

  2. 生成服务端证书 caelog.key caelog.csr

     ``` shell
     openssl req -new -newkey rsa:4096 -keyout caelog.key -out caelog.csr
     ```

  3. 利用根证书向服务端证书签名

     ``` shell
     openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in caelog.csr -out caelog.crt -days 365 -CAcreateserial -extfile conf.config
     ```

  4. 打包证书和私钥到pkcs文件 caelog.p12

     ``` shell
     openssl pkcs12 -export -out caelog.p12 -name "caelog" -inkey caelog.key -in caelog.crt
     ```

  5. springboot开启https

     ``` yaml
     ssl:    key-store: classpath:caelog.p12    key-store-password: 123456    key-store-type: PKCS12    key-alias: caelog    trust-store: classpath:caelog.jks    trust-store-password: 123456    client-auth: want
     ```

     <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/73ac162ca6f34e35a80811a283418505image-20210621143808324.png" width="600px" />

* 未登录系统的用户仅能操作index主页面以及login登录页面

  通过security开放无需登录即可访问的接口，拦截其他端口；同时在前端vue中存储一个当前用户信息，如果用户信息为空则router无法跳转到其他页面。

  ``` java
  @Override    protected void configure(HttpSecurity http) throws Exception {        http.csrf().disable();        http.headers().frameOptions().disable();        http.cors();        http.authorizeRequests()                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()                .antMatchers("/login", "/hello","/register", "/logout","/loginbyP12").permitAll()                .anyRequest().authenticated()                .and().exceptionHandling().authenticationEntryPoint(new MyEntryPoint())                .accessDeniedHandler(new MyDeniedHandler());        http.addFilterBefore(authFilter, FilterSecurityInterceptor.class);    }
  ```

  ``` javascript
  let token = Vue.$cookies.get('infosecAuth');    if (token == null || token === '') {      next('/login');    } else {      next();    }
  ```

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/a06bc7a8d136443893acaf8c785c814eimage-20210621162909705.png" width="600px" />

* 只有拥有管理员权限的用户才能访问用户列表信息，普通用户仅能访问自身详细信息

  通过security中的authfilter权限拦截器对请求进行拦截，通过MySecurityMetadataSource设置路径的权限，以及提供获取请求路径所需的权限，然后在MyDecisionManager中判断当前用户权限是否满足请求路径所需权限；最后如果遇到权限不匹配的则用MyDeniedHandler权限拦截异常处理器处理异常。

  ``` java
  SecurityMetadata securityMetadata = new SecurityMetadata("GET:/users", Arrays.asList(new String[]{"ROLE_ADMIN"}));        RESOURCES.add(securityMetadata);
  ```

  ``` java
  @Override    public Collection<ConfigAttribute> getAttributes(Object object) {        FilterInvocation filterInvocation = (FilterInvocation) object;        HttpServletRequest request = filterInvocation.getRequest();        for (SecurityMetadata resource : RESOURCES) {            String[] split = resource.getPath().split(":");            AntPathRequestMatcher ant = new AntPathRequestMatcher(split[1]);            if (request.getMethod().equals(split[0]) && ant.matches(request)) {                return Collections.singletonList(new SecurityConfig(resource.getAuthors().toString().substring(1,resource.getAuthors().toString().length()-1)));            }        }        return null;    }
  ```

  ``` java
  public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {        if (configAttributes.isEmpty()) {            return;        }        for (ConfigAttribute ca : configAttributes) {            for (GrantedAuthority authority : authentication.getAuthorities()) {                if (Objects.equals(authority.getAuthority(), ca.getAttribute())) {                    return;                }            }        }        throw new AccessDeniedException("没有相关权限");    }
  ```

  ``` java
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {        response.setContentType("application/json;charset=utf-8");        PrintWriter out = response.getWriter();        out.write(JSON.toJSONString(Result.fail("没有相关权限")));        out.flush();        out.close();    }
  ```

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/0d5e80b7b3164a8aaf53244e3bd44c52image-20210621162953540.png" width="600px" />

* 系统支持数字证书的方式进行用户认证，从而替代用户名/密码机制

  利用配置ssl证书时生成的rootCA根证书创建并签证客户端证书，且在生成客户端证书信息时设置好用户名密码，加入到浏览器中，客户端向服务端发送请求时携带证书，服务端通过x509获取证书，判断是否有效，获取其中的用户名密码，用于登录。

  ``` shell
  openssl req -new -newkey rsa:4096 -nodes -keyout shurlormes.key -out shurlormes.csropenssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in shurlormes.csr -out shurlormes.crt -days 365 -CAcreateserialopenssl pkcs12 -export -out shurlormes.p12 -name "shurlormes" -inkey shurlormes.key -in shurlormes.crt
  ```

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/c181fd4fbc724692b703190b093d9169image-20210621151105888.png" width="600px" />

  ```java
  @RequestMapping(value="/loginbyP12")    public Result loginP12(HttpServletRequest request){        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");        if(certs != null) {            X509Certificate gaX509Cert = certs[0];            String dn = gaX509Cert.getSubjectDN().toString();            System.out.println(gaX509Cert.getSubjectDN());            System.out.println("个人证书信息:" + dn);            String username = "";            String[] dnArray = dn.split(",");            for (String dnItem : dnArray) {                String[] dnInfo = dnItem.split("=");                String key = dnInfo[0];                String value = dnInfo[1];                if("cn".equalsIgnoreCase(key.trim())) {                    username = value;                    break;                }            }            String password=username.split("@")[1];            username=username.split("@")[0];            System.out.println(username+"login using p12");            Authentication token = new UsernamePasswordAuthenticationToken(username, password);            Authentication authentication = authenticationManager.authenticate(token);            SecurityContextHolder.getContext().setAuthentication(authentication);            HashMap res = new HashMap();            res.put("token", token);            return Result.succ(token);        }        return Result.fail("缺少客户端证书！");    }
  ```

* 数据库系统具有一定的安全性配置，如防止敏感信息泄露等

  用户密码信息通过BCrypt加密算法保存到mysql中，后续需要校验用户密码时在java中利用BCryptPasswordEncoder对密码进行校验。同时数据库通过GRANT赋予普通用户对user数据表select，不开放修改权限。

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/4a4990f9449848fea934dd40d6848b9cimage-20210621143612292.png" width="600px" />

* 服务器具有一定安全性配置，如防止恶意ping等

  通过修改配置文件使得用户使用ping不做任何反应

  ``` shell
  echo 1 > /proc/sys/net/ipv4/icmp_echo_ignore_all
  ```

  远程5分钟误操作自动注销

  ``` shell
  vim /etc/profileexport TMOUT=300
  ```

* 内网公网均可访问系统

  通过openvpn搭建openvpn服务器，将ip分配在10.8.0.0/24中，部署完后得到服务器内网ip为10.8.0.1，然后在Web服务器利用证书相关文件连接openvpn，得到内网ip为10.8.0.10；在windows利用证书相关文件连接openvpn得到内网ip为10.8.0.14。在浏览器中访问https://www.caelog.top/infosec 成功从公网访问，在浏览器中访问<a>10.8.0.10/infosec</a>成功从内网访问。

  windows客户端分配到的内网ip为10.8.0.14

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/6ac2e56c11224693b36c53b4596ded04image-20210623094742283.png" width="600px" />

  web服务器分配到的内网ip为10.8.0.10

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/10dc29b6be0142de8da157352339d429image-20210623094856576.png" width="600px" />

  外网访问成功

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/c33a1a9920894c8ca020031798ba856cimage-20210623095018759.png" width="600px" />

  内网访问成功

  <img src="https://caelog.oss-cn-beijing.aliyuncs.com/userdata/4/2021/06/23/0b1ab180bacd4db1a9892a1b02019f7fimage-20210623094959751.png" width="600px" />
