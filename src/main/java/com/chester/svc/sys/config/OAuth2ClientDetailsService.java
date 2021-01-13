package com.chester.svc.sys.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 自定义client详细信息类
 */
@Component
public class OAuth2ClientDetailsService implements ClientDetailsService {

    private ClientDetailsService clientDetailsService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        InMemoryClientDetailsServiceBuilder inMemoryClientDetailsServiceBuilder = new InMemoryClientDetailsServiceBuilder();
        inMemoryClientDetailsServiceBuilder
                .withClient("app")
                .secret(passwordEncoder.encode("secret"))
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all")
                .and()
                .withClient("app-mobile")
                .secret(passwordEncoder.encode("secret"))
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all");
        try {
            clientDetailsService = inMemoryClientDetailsServiceBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        if (clientId == null) {
            throw new ClientRegistrationException("客户端不存在");
        }
        return clientDetailsService.loadClientByClientId(clientId);
    }
}
