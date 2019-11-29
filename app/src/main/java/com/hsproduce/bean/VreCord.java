package com.hsproduce.bean;

import java.util.Date;

public class VreCord {
    //ID
    private String id;
    //计划ID
    private String planid;
    //工厂
    private String fac;
    //生产日期
    private String wdate;
    //机台
    private String mchid;
    //LR
    private String lr;
    //is_h
    private String iS_H;
    //产品规格代码
    private String itnbr;
    //产品规格名称
    private String itdsc;
    //班次
    private String shift;
    //班组
    private String team;
    //状态
    private String state;
    //类型
    private String type;
    //胎胚规格代码
    private String itnbrt;
    //胎胚规格名称
    private String itdsct;
    //条码
    private String barcode;
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

    public String getiS_H() {
        return iS_H;
    }

    public void setiS_H(String iS_H) {
        this.iS_H = iS_H;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanid() {
        return planid;
    }

    public void setPlanid(String planid) {
        this.planid = planid;
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

    public String getLr() {
        return lr;
    }

    public void setLr(String lr) {
        this.lr = lr;
    }

    public String getItnbr() {
        return itnbr;
    }

    public void setItnbr(String itnbr) {
        this.itnbr = itnbr;
    }

    public String getItdsc() {
        return itdsc;
    }

    public void setItdsc(String itdsc) {
        this.itdsc = itdsc;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItnbrt() {
        return itnbrt;
    }

    public void setItnbrt(String itnbrt) {
        this.itnbrt = itnbrt;
    }

    public String getItdsct() {
        return itdsct;
    }

    public void setItdsct(String itdsct) {
        this.itdsct = itdsct;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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
}
