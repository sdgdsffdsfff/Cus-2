package com.suning.cus.utils;

import android.content.Context;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;

/**
 * Created by 14110105 on 2015/3/19.
 */
public class XUtilsHelper {

    public static final int ON_SUCCESS = 1;

    public static final int ON_FAILED = -1;

    public static final int ON_CANCEL = 0;

    private static HttpUtils client;

    public XUtilsHelper(Context context) {

        client = getInstance(context);

    }

    public HttpClient getHttpClient() {
        return client.getHttpClient();
    }


    public void sendPost(final String url, final RequestParams params, final RequestCallBack<String> callback) {
        client.send(HttpRequest.HttpMethod.POST, url, params, callback);
    }

    public void configCookieStore(CookieStore cookieStore) {
        if (client != null) {
            client.configCookieStore(cookieStore);
        }
    }

    public synchronized static HttpUtils getInstance(Context context) {

        if(client == null) {
            client = new HttpUtils();
            // 设置请求超时时间为10秒
            client.configSoTimeout(1000 * 10);
            // 设置返回数据的编码格式
            client.configResponseTextCharset("UTF-8");
            // 保存服务器端(Session)的Cookie
            //PreferencesCookieStore cookieStore = new PreferencesCookieStore(context);
            //cookieStore.clear();
            //client.configCookieStore(cookieStore);

            // 15010551 本地读取CookieStore
            client.configCookieStore(SpCoookieUtils.getCookieStore(context));

        }
//        XUtilsHelper.context = context;
        return  client;
    }
}
