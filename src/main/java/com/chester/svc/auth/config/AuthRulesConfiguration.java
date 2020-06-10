package com.chester.svc.auth.config;

import com.chester.auth.client.AuthRule;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "chester")
@Data
public class AuthRulesConfiguration {
    private List<AuthRule> rules;
}
