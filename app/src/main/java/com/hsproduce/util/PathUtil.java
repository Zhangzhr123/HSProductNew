package com.hsproduce.util;

import com.hsproduce.App;
import com.hsproduce.activity.LoginActivity;
import com.hsproduce.activity.VulcanizationActivity;

public interface PathUtil {
    //服务ip地址
    public static final String SERVER = "http://"+ App.ip +"/";

    //登录
    public static final String LOGIN =  SERVER + "api/PDA/PdaLogin";
    //获取班组
    public static final String GET_SHIFT = SERVER + "api/User/GetTeam";
    //注销
    public static final String LOGOUT =  SERVER + "login/logout";

    //获取菜单
    public static final String GET_TEAM =SERVER + "api/PDA/GetTeam";

    //根据机台号获取计划
    public static final String VUL_GET_PLAN = SERVER + "api/PDA/GetVPlan";
    //判断轮胎条码是否重复
    public static final String VUL_SelActual_TYRE_CODE = SERVER + "api/PDA/SelActual_TYRE_CODE";
    //扫描条码，新增生产实绩
    public static final String VUL_AddActualAchievement = SERVER + "api/PDA/AddActualAchievement";

    //根据状态查询计划
    public static final String GetCurrentVPlan = SERVER + "api/PDA/GetCurrentVPlan";
    //规格交替
    public static final String SwitchVplan = SERVER + "api/PDA/SwitchVplan";

    //根据轮胎条码查询轮胎规格
    public static final String SelTYRE_CODE = SERVER + "api/PDA/SelTYRE_CODE";
    //条码更换
    public static final String ChangeTYRE_CODE = SERVER + "api/PDA/ChangeTYRE_CODE";

    //根据规格编码模糊查询规格
    public static final String GetSPECIFICATION = SERVER + "api/PDA/GetSPECIFICATION";
    //根据TYPEID 获取数据字典内容
    public static final String GetDictionaries = SERVER + "api/PDA/GetDictionaries";
    //根据条件获取计划
    public static final String GetVPlan_T = SERVER + "api/PDA/GetVPlan_T";
    //条码补录
    public static final String SupplementTYRE_CODE = SERVER + "api/PDA/SupplementTYRE_CODE";

    //根据条码查询轮胎信息
    public static final String SelDetailed = SERVER + "api/PDA/SelDetailed";
    //明细变更
    public static final String ChangeDetailed = SERVER + "api/PDA/ChangeDetailed";

    //质检标记接口
    public static final String QualityTesting = SERVER + "api/PDA/QualityTesting";
    //根据数据字典获取不合格原因
    public static final String ERRORGetDictionaries = SERVER + "api/PDA/GetDictionaries";

    //获取装车单信息
    public static final String SelVLOADList = SERVER + "api/PDA/SelVLOADList";
    //根据ID获取装车单明细
    public static final String SelVLOADListMX = SERVER + "api/PDA/SelVLOADListMX";
    //出厂扫描
    public static final String InsVLOAD = SERVER + "api/PDA/InsVLOAD";
    //取消扫描
    public static final String DelVLOAD = SERVER + "api/PDA/DelVLOAD";
    //发送WMS
    public static final String SendWMS = SERVER + "api/PDA/SendWMS";

    //退厂扫描
    public static final String OutsVLOAD = SERVER + "api/PDA/BarcordReturn";

    //APP更新
    public static final String 获取最新版本 = SERVER + "UpdateVersion/getNewVersion";
    //APK文件下载
    public static final String 文件下载 = SERVER + "file/download?filepath=";

    //成型生产
    public static final String FORMINGPLAN = SERVER + "api/PDA/GetFormingVPlan";
    //成型生产
    public static final String StartProduction = SERVER + "api/PDA/StartProduction";
    //成型规格交替
    public static final String SWITCHFORMINGPLAN = SERVER + "api/PDA/GetCurrentVPlan_Forming";
    //成型胚胎报废
    public static final String FORMINGBARCODE = SERVER + "";
    //成型生产变更
    public static final String FORMINGCHANGE = SERVER + "";
    //生产追溯
    public static final String BARCODEDETAIL = SERVER + "";

}
