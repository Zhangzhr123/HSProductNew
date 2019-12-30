package com.hsproduce.util;

import com.hsproduce.App;
import com.hsproduce.activity.LoginActivity;
import com.hsproduce.activity.VulcanizationActivity;

public interface PathUtil {
    //服务ip地址
    public static final String SERVER = "http://" + App.ip + "/";
    //登录
    public static final String LOGIN = SERVER + "api/PDA/PdaLogin";
    //获取班组
    public static final String GET_SHIFT = SERVER + "api/User/GetTeam";
    //注销
    public static final String LOGOUT = SERVER + "login/logout";
    //修改密码
    public static final String UPDATEPW = SERVER + "api/PDA/GetUpdatePassword";
    //获取菜单
    public static final String GET_TEAM = SERVER + "api/PDA/GetTeam";
    //硫化生产根据机台号获取计划
    public static final String VUL_GET_PLAN = SERVER + "api/PDA/GetVPlan";
    //判断轮胎条码是否重复
    public static final String VUL_SelActual_TYRE_CODE = SERVER + "api/PDA/SelActual_TYRE_CODE";
    //扫描条码，新增生产实绩
    public static final String VUL_AddActualAchievement = SERVER + "api/PDA/AddActualAchievement";
    //判断规格是否一致
    public static final String ErrorJudge = SERVER +"api/PDA/ErrorJudge";
    //硫化生产根据状态查询计划
    public static final String GetCurrentVPlan = SERVER + "api/PDA/GetCurrentVPlan";
    //规格交替
    public static final String SwitchVplan = SERVER + "api/PDA/SwitchVplan";
    //硫化生产根据轮胎条码查询轮胎规格
    public static final String SelTYRE_CODE = SERVER + "api/PDA/SelTYRE_CODE";
    //条码更换
    public static final String ChangeTYRE_CODE = SERVER + "api/PDA/ChangeTYRE_CODE";
    //硫化生产根据规格编码模糊查询规格
    public static final String GetSPECIFICATION = SERVER + "api/PDA/GetSPECIFICATION";
    //根据TYPEID 获取数据字典内容  机台号
    public static final String GetDictionaries = SERVER + "api/PDA/GetDictionaries";
    //根据条件获取计划
    public static final String GetVPlan_T = SERVER + "api/PDA/GetVPlan_T";
    //条码补录
    public static final String SupplementTYRE_CODE = SERVER + "api/PDA/SupplementTYRE_CODE";
    //根据条码查询轮胎信息       生产追溯硫化信息
    public static final String SelDetailed = SERVER + "api/PDA/SelDetailed";
    //明细变更
    public static final String ChangeDetailed = SERVER + "api/PDA/ChangeDetailed";
    //质检标记接口
    public static final String QualityTesting = SERVER + "api/PDA/QualityTesting";
    //根据数据字典获取不合格原因
//    public static final String ERRORGetDictionaries = SERVER + "api/PDA/GetDictionaries";
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
    public static final String 获取最新版本 = SERVER + "api/PDA/GetVERSION";
    //APK文件下载
    public static final String 文件下载 = SERVER + "hstm.apk";
    //成型生产                  获取生产计划
    public static final String FORMINGPLAN = SERVER + "api/PDA/GetFormingVPlan";
    //开始生产计划
//    public static final String StartProduction = SERVER + "api/PDA/StartProduction";
    //成型查询正在生产或者等待中的计划
//    public static final String SWITCHFORMINGPLAN = SERVER + "api/PDA/GetCurrentVPlan_Forming";
    //成型规格交替
//    public static final String SWITCHFORMING = SERVER + "api/PDA/SwitchVplan_Forming";
    //成型胚胎报废
    public static final String FORMINGBARCODE = SERVER + "api/PDA/FormingScrap";
    //成型生产根据条码查询明细   生产追溯成型信息
    public static final String FORMINGSECLECTCODE = SERVER + "api/PDA/SelDetailed_Forming";
    //成型生产模糊查询规格
    public static final String FORMINGSECLECTITNBR = SERVER + "api/PDA/GetSPECIFICATION_Forming";
    //成型查询机台号
    public static final String FORMINGSELECTMCHID = SERVER + "api/PDA/GetDictionaries";
    //成型明细变更
    public static final String FORMINGCHANGE = SERVER + "api/PDA/ChangeDetailed_Forming";
    //成型生产 查询生产中的计划
    public static final String START = SERVER + "api/PDA/GetCurrentVPlan_Forming_New";
    //成型生产 开始计划
    public static final String GETSTART = SERVER + "api/PDA/StartVplan";
    //成型生产 修改计划
    public static final String UPDATE = SERVER + "api/PDA/UpdateVplan";
    //成型生产 完成计划
    public static final String FINISH = SERVER + "api/PDA/CompleteVplan";
    //查询成型计划展示
    public static final String GetCurrDateFormingVPlan = SERVER + "api/PDA/GetCurrDateFormingVPlan";
    //成型取消扫描
    public static final String OUTFORMINGBARCODE = SERVER + "";
    //硫化增加查看本班次扫描数量
    public static final String GetDnumSum = SERVER + "api/PDA/GetDnumSum";
    //硫化取消扫描
    public static final String OUTVULBARCODE = SERVER + "";
}
