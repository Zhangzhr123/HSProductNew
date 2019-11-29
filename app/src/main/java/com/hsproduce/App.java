package com.hsproduce;

import android.app.Application;
import com.google.gson.Gson;
import com.xuexiang.xui.XUI;

public class App extends Application {

    public static Gson gson = new Gson();
    public static String username = "";
    public static String shift = "";
    public static String lr = "";
    public static String access_token = "";
    public static String Iu = "";
    public static String ip = "";//27191q95f3.wicp.vip:21127
    @Override
    public void onCreate() {
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        super.onCreate();
    }

}
