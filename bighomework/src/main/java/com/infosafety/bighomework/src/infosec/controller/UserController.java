package com.infosafety.bighomework.src.infosec.controller;

import java.security.cert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.infosafety.bighomework.src.infosec.dao.UserMapper;
import com.infosafety.bighomework.src.infosec.model.User;
import com.infosafety.bighomework.src.infosec.service.UserDetailServiceImpl;
import com.infosafety.bighomework.src.infosec.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = "register")
    public String register(){
        return "register";
    }

    @RequestMapping(value = "hello")
    public Result hello() {
        return Result.succ("hello");
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public Result listUsers() {

        try {
            List<User> users = userMapper.listUsers();
            return Result.succ(users);

        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail("error!");
        }

    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Result getUser(@RequestParam("id") String id) {
        try {
            User user = userMapper.getUser(id);

            return Result.succ(user);

        } catch (Exception ex) {
            return Result.fail("error");
        }

    }

    @RequestMapping(value = "/getUserByName", method = RequestMethod.GET)
    public Result getUserbyName(@RequestParam("name") String name) {
        try {
            User user = userMapper.getUserbyName(name);

            return Result.succ(user);

        } catch (Exception ex) {
            return Result.fail("error");
        }

    }

    @RequestMapping(value="/loginbyP12")
    public Result loginP12(HttpServletRequest request){
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        if(certs != null) {
            X509Certificate gaX509Cert = certs[0];
            String dn = gaX509Cert.getSubjectDN().toString();
            System.out.println(gaX509Cert.getSubjectDN());
            System.out.println("个人证书信息:" + dn);
            String username = "";
            String[] dnArray = dn.split(",");
            for (String dnItem : dnArray) {
                String[] dnInfo = dnItem.split("=");
                String key = dnInfo[0];
                String value = dnInfo[1];
                if("cn".equalsIgnoreCase(key.trim())) {
                    username = value;
                    break;
                }
            }
            String password=username.split("@")[1];
            username=username.split("@")[0];
            System.out.println(username+"login using p12");
            Authentication token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HashMap res = new HashMap();
            res.put("token", token);
            return Result.succ(token);
        }
        return Result.fail("缺少客户端证书！");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Result login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Authentication token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HashMap res = new HashMap();
        res.put("token", token);
        return Result.succ(token);
    }

    @RequestMapping(value = "/logoutuser", method = RequestMethod.GET)
    public Result logout() {
        System.out.println("logout");
        SecurityContextHolder.clearContext();
        return Result.succ("logout successfully");
    }

    public UserMapper getUserMapper() {
        return userMapper;
    }


    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
