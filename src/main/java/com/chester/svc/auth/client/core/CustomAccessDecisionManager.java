package com.chester.svc.auth.client.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;

@Slf4j
public class CustomAccessDecisionManager implements AccessDecisionManager {

    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {

        for (ConfigAttribute attribute : configAttributes) {
            if (authentication == null) {
                throw new AccessDeniedException("当前访问没有权限");
            }
            String needCode = attribute.getAttribute();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().toUpperCase().equals(needCode.toUpperCase())) {
                    return;
                }
            }
        }

        throw new AccessDeniedException("当前访问没有权限");
    }

    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}