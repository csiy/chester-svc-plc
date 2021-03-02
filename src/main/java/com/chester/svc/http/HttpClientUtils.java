package com.chester.svc.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
public class HttpClientUtils {

    private static CloseableHttpClient httpClient = SpringContextUtil.getBean(CloseableHttpClient.class);


    /**
     * get 请求  Content-Type==text/html;charset=UTF-8
     *
     * @param url       url地址
     * @param paramsObj params参数组成的Object对象
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static <T> String doGet(String url, Object paramsObj) throws IOException, URISyntaxException {
        Map<String, String> params = JSON.parseObject(JSON.toJSONString(paramsObj), Map.class);
        return doGet(url, params, -1);
    }

    public static <T> String doGet(String url, Object paramsObj, int socketTimeout) throws IOException, URISyntaxException {
        Map<String, String> params = JSON.parseObject(JSON.toJSONString(paramsObj), Map.class);
        return doGet(url, params, socketTimeout);
    }


    /**
     * post调用  使用配置文件中配置的超时时间
     *
     * @param url          请求地址
     * @param paramsObj    请求实体
     * @param responseType 请求内容  例子：new TypeReference<List<Account>>(){}
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T doPost(String url, Object paramsObj, TypeReference<T> responseType) throws IOException {
        return doPost(url, paramsObj, responseType, -1);
    }

    public static String doPost(String url, Object paramsObj) throws IOException {
        return doPost(url, paramsObj, -1);
    }

    /**
     * post请求  Content-Type=application/json;charset=UTF-8
     *
     * @param url           url地址
     * @param paramsObj     请求参数域
     * @param responseType  响应对象类型
     * @param socketTimeout 超时时间
     * @param <T>
     * @return 响应实体对应的内容
     * @throws IOException
     */
    public static <T> T doPost(String url, Object paramsObj, TypeReference<T> responseType, int socketTimeout) throws IOException {
        String responseContent = doPost(url, paramsObj, socketTimeout);
        if (StringUtils.isBlank(responseContent)) {
            return null;
        }

        T response = JSONObject.parseObject(responseContent, responseType);

        return response;
    }


    /**
     * @param url
     * @param paramsObj
     * @param socketTimeout
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Object paramsObj, int socketTimeout) throws IOException {
        HttpPost post = new HttpPost(url);
        //若上送String类型对象，无需进行String类型转换
        String paramsStr = paramsObj instanceof String ? (String) paramsObj : JSONObject.toJSONString(paramsObj);
        StringEntity entity = new StringEntity(paramsStr, "UTF-8");
        post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        post.setEntity(entity);
        return doHttp(post, socketTimeout);
    }

    /**
     * get 请求  Content-Type==text/html;charset=UTF-8
     *
     * @param url    url地址
     * @param params params参数组成的Map对象
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String doGet(String url, Map<String, String> params) throws IOException, URISyntaxException {
        return doGet(url, params, -1);
    }


    public static String doGet(String url, Map<String, String> params, int socketTimeout) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.setCharset(Consts.UTF_8).build();
        if (params != null) {
//            Set<String> keys = params.keySet();
//            for (String key : keys) {
//                uriBuilder.addParameter(key, params.get(key));
//            }
            params.forEach(uriBuilder::addParameter);
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        //设置请求头
        httpGet.addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8");

        return doHttp(httpGet, socketTimeout);
    }


    /**
     * 实际上调用远程的方法
     *
     * @param request       httpGet/httpPost的共同父类
     * @param socketTimeout 超时时间
     * @return
     * @throws IOException
     */
    private static String doHttp(HttpRequestBase request, int socketTimeout) throws IOException {
        //设置超时时间
        if (socketTimeout > 0) {
            //获取原有配置
            //实际注入类型org.apache.http.impl.client.InternalHttpClient
            Configurable configClient = (Configurable) httpClient;
            RequestConfig.Builder custom = RequestConfig.copy(configClient.getConfig());
            //设置个性化配置
            RequestConfig config = custom.setSocketTimeout(socketTimeout).build();
            request.setConfig(config);
        }
        ResponseHandler<String> handler = new BasicResponseHandler();
        long startPoint = System.currentTimeMillis();
        String response = httpClient.execute(request, handler);
        log.info("请求耗时【{}】, 接口返回信息【{}】", System.currentTimeMillis() - startPoint, response);
        return response;
    }

    public static String postWithParamsForString(String url, List<NameValuePair> params){
        HttpPost httpPost =  new HttpPost(url);
        String s = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode==200){
                HttpEntity entity = response.getEntity();
                s = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            log.info("发送失败");
        }
        return s;
    }
}