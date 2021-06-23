package com.infosafety.bighomework.src.infosec.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class SecurityMetadata {
    private String path;
    private List<GrantedAuthority>authors;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<GrantedAuthority> getAuthors() {
        return authors;
    }

    public void setAuthors(List<GrantedAuthority> authors) {
        this.authors = authors;
    }

    public SecurityMetadata(String path,List<String>roles){
        this.authors=new ArrayList<>();
        this.path=path;
        for(String role:roles){
            this.authors.add(new SimpleGrantedAuthority(role));
        }
    }
}
