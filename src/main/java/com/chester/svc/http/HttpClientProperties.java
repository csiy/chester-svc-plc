package com.chester.svc.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.http-pool")
public class HttpClientProperties {
    //默认配置
    private int defaultMaxPerRoute = 2;
    private int maxTotal = 20;
    private int validateAfterInactivity = 2000;
    private int connectTimeout = 2000;
    private int connectionRequestTimeout = 20000;
    private int socketTimeout = 20000;

}