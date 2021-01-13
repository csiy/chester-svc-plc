package com.chester.svc.sys.config;

import com.chester.svc.sys.db.model.Role;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.util.coll.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义用户信息服务
 */
@Slf4j
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private UserRepository userRepository;

    private List<SimpleGrantedAuthority> transferRoles(List<Role> roles, boolean addRoleAuthed) {
        List<SimpleGrantedAuthority> authorities = Lists.map(Lists.map(roles, Role::getName), SimpleGrantedAuthority::new);
        if (addRoleAuthed) {
            authorities.add(new SimpleGrantedAuthority("authed"));
        }
        return authorities;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.chester.svc.sys.db.model.User account = userRepository.getByUserIdOrPhone(Long.valueOf(username), username);
        if (account != null) {
            boolean enabled = !Boolean.TRUE.equals(account.getIsDisabled());
            List<Role> roles = Stream.of(account.getRoles().toArray(new Role[]{})).collect(Collectors.toList());
            List<? extends GrantedAuthority> authorities = this.transferRoles(roles, true);
            return new User(String.valueOf(account.getUserId()), account.getPassword(), enabled, true, true, true, authorities);
        } else {
            throw new UsernameNotFoundException("用户不存在");
        }
    }
}