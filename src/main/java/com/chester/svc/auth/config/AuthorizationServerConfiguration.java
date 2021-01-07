package com.chester.svc.auth.config;

import com.chester.svc.auth.exception.UserOAuth2WebResponseExceptionTranslator;
import com.chester.svc.auth.service.OAuth2ClientDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private OAuth2ClientDetailsService oAuth2ClientDetailsService;
    @Resource
    private TokenStore tokenStore;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //客户端详情信息在这里进行初始化，你能够把客户端详情信息写死在这里或者是通过数据库来存储调取详情信息
        clients.withClientDetails(oAuth2ClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")// 开启/oauth/token_key验证端口无权限访问
                .checkTokenAccess("isAuthenticated()");// 开启/oauth/check_token验证端口认证权限访问
        security.allowFormAuthenticationForClients().passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        //token持久化容器
        tokenServices.setTokenStore(tokenStore);
        //客户端信息
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        //access_token 的有效时长 (秒), 默认 12 小时
        tokenServices.setAccessTokenValiditySeconds(43200);//12小时
        //refresh_token 的有效时长 (秒), 默认 30 天
        tokenServices.setRefreshTokenValiditySeconds(5184000);//2月
        //是否支持refresh_token，默认false
        tokenServices.setSupportRefreshToken(true);
        //是否复用refresh_token,默认为true(如果为false,则每次请求刷新都会删除旧的refresh_token,创建新的refresh_token)
        tokenServices.setReuseRefreshToken(true);
        //token相关服务
        endpoints.tokenStore(tokenStore)
                //自定义refresh_token刷新令牌对用户信息的检查，以确保用户信息仍然有效
                .userDetailsService(userDetailsService)
                //通过authenticationManager开启密码授权
                .authenticationManager(authenticationManager)
                .exceptionTranslator(new UserOAuth2WebResponseExceptionTranslator());
        ;
    }

}
