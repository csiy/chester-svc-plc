package com.chester.svc.http;

import org.apache.http.NameValuePair;

import java.util.List;

public interface ClientUtil {

    String postWithParamsForString(String url, List<NameValuePair> params);
}
