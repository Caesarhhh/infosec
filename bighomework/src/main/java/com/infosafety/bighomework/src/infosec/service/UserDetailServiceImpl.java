package com.infosafety.bighomework.src.infosec.service;

import com.infosafety.bighomework.src.infosec.dao.UserMapper;
import com.infosafety.bighomework.src.infosec.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userMapper.listUsers();
        User user=null;
        for(int i=0;i<users.size();i++){
            if(users.get(i).getUsername().equals(username)){
                user=users.get(i);
            }
        }
        if (user == null)
            throw new UsernameNotFoundException("找不到该账户信息！");

        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

        getRoles(user, list);

        org.springframework.security.core.userdetails.User auth_user = new
                org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), list);//返回包括权限角色的User给security
        return auth_user;
    }

    public void getRoles(User user, List<GrantedAuthority> list) {
        for (String role : user.getRolecode().split(",")) {
            list.add(new SimpleGrantedAuthority(role));
        }
    }

}
