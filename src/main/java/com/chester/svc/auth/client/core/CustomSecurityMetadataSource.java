package com.chester.svc.auth.client.core;

import com.chester.svc.auth.db.model.AuthRule;
import com.chester.util.coll.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final Map<AuthRule, Collection<ConfigAttribute>> metadataSource = new HashMap<>();

    public void setSource(AuthRule rule){
        Collection<ConfigAttribute> configAttributes = new ArrayList<>(Lists.map(rule.getRoles().split(","), this::createdConfigAttribute));
        metadataSource.put(rule,configAttributes);
    }

    private ConfigAttribute createdConfigAttribute(String config){
        return new SecurityConfig(config);
    }

    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        for (Map.Entry<AuthRule, Collection<ConfigAttribute>> entry : metadataSource.entrySet()) {
            AuthRule authRule = entry.getKey();
            String method = fi.getRequest().getMethod();
            RequestMatcher requestMatcher = new AntPathRequestMatcher(authRule.getPath());
            if (requestMatcher.matches(fi.getHttpRequest())&&authRule.getType().equals(method)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

}
