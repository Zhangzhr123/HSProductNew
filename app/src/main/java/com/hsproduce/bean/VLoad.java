package com.hsproduce.bean;

import java.util.Date;

public class VLoad {
    //ID号
    private String id;
    //工厂
    private String fac;
    //日期
    private String wdate;
    //发货口
    private String mchid;
    //装车单号
    private String loadno;
    //班次
    private String shift;
    //状态
    private String state;
    //创建人
    private String createuser;
    //创建时间
    private Date createtime;
    //修改人
    private String updateuser;
    //修改时间
    private Date updatetime;
    //备注
    private String remark;
    //删除标识
    private Integer deleteflag;
    //仓库代码
    private String suB_CODE;
    //装卸组编码
    private String loadunload;
    //车牌号
    private String caR_CODE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFac() {
        return fac;
    }

    public void setFac(String fac) {
        this.fac = fac;
    }

    public String getWdate() {
        return wdate;
    }

    public void setWdate(String wdate) {
        this.wdate = wdate;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getLoadno() {
        return loadno;
    }

    public void setLoadno(String loadno) {
        this.loadno = loadno;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(Integer deleteflag) {
        this.deleteflag = deleteflag;
    }

    public String getSuB_CODE() {
        return suB_CODE;
    }

    public void setSuB_CODE(String suB_CODE) {
        this.suB_CODE = suB_CODE;
    }

    public String getLoadunload() {
        return loadunload;
    }

    public void setLoadunload(String loadunload) {
        this.loadunload = loadunload;
    }

    public String getCaR_CODE() {
        return caR_CODE;
    }

    public void setCaR_CODE(String suB_CODE) {
        this.caR_CODE = suB_CODE;
    }
}
