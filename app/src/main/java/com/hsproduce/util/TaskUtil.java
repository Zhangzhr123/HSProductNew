package com.hsproduce.util;

import android.text.TextUtils;
import com.hsproduce.App;
import com.hsproduce.bean.Result;

import java.lang.reflect.Type;

public class TaskUtil {

    public static Result handle(Result res, String result, Type type){

        if(!TextUtils.isEmpty(result)){
            try{
                res = App.gson.fromJson(result, type);
            }catch (Exception e){
                e.printStackTrace();
                res.setFlag(false);
                res.setMessage(e.getMessage());
            }
        }else{
            res.setFlag(false);
            res.setMessage("链接服务器失败");
        }

        return res;
    }
}
