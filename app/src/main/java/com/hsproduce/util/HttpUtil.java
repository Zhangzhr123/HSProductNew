package com.hsproduce.util;

import com.hsproduce.App;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

import java.io.IOException;

public class HttpUtil {

    static CloseableHttpClient httpclient = HttpClients.createDefault();
    public final static String formContent = "application/x-www-form-urlencoded";
    public final static String jsonContent = "application/json";
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        HttpGet httpGet = new HttpGet(url + "?" + param);
        CloseableHttpResponse response1 = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);

            response1 = httpclient.execute(httpGet);
            if(response1.getStatusLine().getStatusCode() == 200) {
                // 获取相应字符串
                result = EntityUtils.toString(response1.getEntity());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                if(response1 != null){
                    response1.close();
                }
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送post请求
     *
     * @return 响应的数据
     */
    public static String sendPost(String url, String params, String contentType) {
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response2 = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            StringEntity s = new StringEntity(params, "UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType(contentType);
            httpPost.setEntity(s);
            if(!StringUtil.isNullOrBlank(App.access_token)){
                httpPost.addHeader("Authorization", "Bearer " + App.access_token);
            }
            response2 = httpclient.execute(httpPost);
            if(response2.getStatusLine().getStatusCode() == 200) {
                // 获取相应字符串
                result = EntityUtils.toString(response2.getEntity());
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(response2 != null){
                    response2.close();
                }
            }catch(IOException ioe){

            }
        }
        return result;
    }

    /**
     * 发送put请求
     *
     * @return 响应的数据
     */
    public static String sendPut(String url, String params, String contentType) {
        String result = "";
        HttpPut httpPut = new HttpPut(url);
        CloseableHttpResponse response2 = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).build();//设置请求和传输超时时间
            httpPut.setConfig(requestConfig);
            StringEntity s = new StringEntity(params, "UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType(contentType);
            httpPut.setEntity(s);
            if(!StringUtil.isNullOrBlank(App.access_token)){
                httpPut.addHeader("Authorization", "Bearer " + App.access_token);
            }
            response2 = httpclient.execute(httpPut);
            if(response2.getStatusLine().getStatusCode() == 200) {
                // 获取相应字符串
                result = EntityUtils.toString(response2.getEntity());
            }else{
                result = EntityUtils.toString(response2.getEntity());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(response2 != null){
                    response2.close();
                }
            }catch(IOException ioe){

            }
        }
        return result;
    }

}
