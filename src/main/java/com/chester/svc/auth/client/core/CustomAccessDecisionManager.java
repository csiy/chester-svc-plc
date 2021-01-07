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
import java.util.Iterator;

@Slf4j
public class CustomAccessDecisionManager implements AccessDecisionManager {

    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {

        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            if (authentication == null) {
                throw new AccessDeniedException("当前访问没有权限");
            }
            ConfigAttribute configAttribute = iterator.next();
            String needCode = configAttribute.getAttribute();
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
        log.info("supports attribute:{}",attribute);
        return true;
    }

    public boolean supports(Class<?> clazz) {
        log.info("supports clazz:{}",clazz);
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}