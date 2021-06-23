package com.infosafety.bighomework.src.infosec.service;

import com.infosafety.bighomework.src.infosec.model.SecurityMetadata;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class MySecurityMetadataSource implements SecurityMetadataSource {
    private static Set<SecurityMetadata> RESOURCES = new HashSet<>();

    @PostConstruct
    private void initRESOURCES() {
        SecurityMetadata securityMetadata = new SecurityMetadata("GET:/users", Arrays.asList(new String[]{"ROLE_ADMIN"}));
        RESOURCES.add(securityMetadata);
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        for (SecurityMetadata resource : RESOURCES) {
            String[] split = resource.getPath().split(":");
            AntPathRequestMatcher ant = new AntPathRequestMatcher(split[1]);
            if (request.getMethod().equals(split[0]) && ant.matches(request)) {
                return Collections.singletonList(new SecurityConfig(resource.getAuthors().toString().substring(1,resource.getAuthors().toString().length()-1)));
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
