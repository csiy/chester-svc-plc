package com.chester.svc.auth.config;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.db.model.AuthRule;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AuthInitConfiguration implements ApplicationRunner {
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        getAllURL();
    }

    public List<AuthRule> getAllURL() {
        List<AuthRule> authRuleList = new ArrayList<>();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
            AuthRule authRule = new AuthRule();
            RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
            HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();
            PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            authRule.setPath(p.getPatterns().iterator().next());
            for(RequestMethod method :methodsCondition.getMethods()){
                authRule.setType(method.toString());
            }
            if(authRule.getType()==null){
                continue;
            }
            parseRolesAnnotation(authRule,handlerMethod.getMethod().getDeclaredAnnotations());
            authRuleList.add(authRule);
        }
        return authRuleList;
    }

    private void parseRolesAnnotation(AuthRule authRule, Annotation[] annotations){
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Roles) {
                    Roles roles = (Roles) annotation;
                    authRule.setRoles(roles.value());
                    authRule.setModify(roles.modify());
                    authRule.setRemark(roles.remark());
                }
            }
        }
    }
}
