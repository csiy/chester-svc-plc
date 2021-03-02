package com.chester.svc.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @program: springboot
 * @description: httpClient通信工具类
 * @author: xueruiye
 * @create: 2019-08-13 17:18
 * <p>
 * 注：设置httpClient的工具类。提供了get和post访问的静态方法。
 * get请求 Content-Type==text/html;charset=UTF-8
 * post请求 Content-Type=application/json;charset=UTF-8
 * 可以灵活的设置socket-timeout（socket连接时间，即超时时间,单位毫秒！）
 */
@Slf4j
@Component
public class MyHttpClientUtils {

    @Async
    public String postWithParamsForString(String url, List<NameValuePair> params) {
        HttpPost httpPost = new HttpPost(url);
        String s = "";
        try {
            CloseableHttpClient httpClient = SpringContextUtil.getBean(CloseableHttpClient.class);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                s = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            log.info("发送失败");
        }
        return s;
    }
}