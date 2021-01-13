package com.chester.svc.sys.config;

import com.chester.svc.sys.db.model.Rule;
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

/**
 * 方法调用安全配置元数据
 */
@Slf4j
@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private static final Map<Rule, Collection<ConfigAttribute>> metadataSource = new HashMap<>();

    public void setSource(List<Rule> rules) {
        Lists.each(rules, this::setSource);
    }

    public void setSource(Rule rule) {
        Collection<ConfigAttribute> configAttributes = new ArrayList<>(Lists.map(rule.getInitRole().split(","), this::createdConfigAttribute));
        metadataSource.put(rule, configAttributes);
    }

    private ConfigAttribute createdConfigAttribute(String config) {
        return new SecurityConfig(config);
    }

    /**
     * 获取方法安全元数据
     */
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        for (Map.Entry<Rule, Collection<ConfigAttribute>> entry : metadataSource.entrySet()) {
            Rule authRule = entry.getKey();
            String method = fi.getRequest().getMethod();
            RequestMatcher requestMatcher = new AntPathRequestMatcher(authRule.getPath());
            if (requestMatcher.matches(fi.getHttpRequest()) && authRule.getType().equals(method)) {
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
