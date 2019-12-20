package com.hsproduce;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.xuexiang.xui.XUI;

import java.io.*;

import static android.support.constraint.Constraints.TAG;

public class App extends Application {

    public static Gson gson = new Gson();
    public static String username = "";
    public static String usercode = "";//工号
    public static String password = "";
    public static String shift = "";//班组
    public static String lr = "";
    public static String access_token = "";
    public static String Iu = "";
    public static String ip = "";//27191q95f3.wicp.vip:21127
    public static String version = "";

    @Override
    public void onCreate() {
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        //如果登录有问题，ip初始化设置放到这里来，自动更新不变
        // 获取设置的IP地址
        String text = get("myIP");
        if (!TextUtils.isEmpty(text)) {
            App.ip = text;
        }
        super.onCreate();
    }

    private String get(String packName) {

        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream in = openFileInput(packName);
            reader = new BufferedReader(new InputStreamReader(in));
            String temp;
            while ((temp = reader.readLine()) != null) {
                content.append(temp);
            }
        } catch (FileNotFoundException e) {
//            Log.e(TAG, "get: FileNotFoundException", e);
        } catch (IOException e) {
            Log.e(TAG, "get: IOException", e);
        }
        return content.toString();
    }

}
