package com.chester.svc.support.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
public class UserAuditor implements AuditorAware<Long> {
 
    @Override
    public Optional<Long> getCurrentAuditor() {
        UserDetails user;
        try {
            user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(user!=null){
                return Optional.of(Long.valueOf(user.getUsername()));
            }else{
                return Optional.empty();
            }
        }catch (Exception e){
            return Optional.empty();
        }
    }
}