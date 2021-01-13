package com.chester.svc.sys.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserUtils {

    private static String getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        String principal = getPrincipal();
        if ("anonymousUser".equals(principal)) {
            return null;
        }
        return principal != null ? Long.parseLong(principal) : null;
    }

    public static List<String> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

}
