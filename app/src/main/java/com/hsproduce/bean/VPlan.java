package com.hsproduce.bean;

import java.util.Date;

public class VPlan {
    //ID号
    private String id;
    //工厂
    private String fac;
    //计划日期
    private String pdate;
    //完成日期
    private String adate;
    //机台
    private String mchid;
    //LR
    private String lr;
    //产品规格代码
    private String itnbr;
    //产品规格名称
    private String itdsc;
    //计划数量
    private String pnum;
    //实绩数量
    private String anum;
    //班次
    private String shift;
    //优先级
    private String pro;
    //状态
    private String state;
    //类型
    private String type;
    //胎胚规格代码
    private String itnbrt;
    //胎胚规格名称
    private String itdsct;
    //开始条码
    private String barcodeStart;
    //结束条码
    private String barcodeEnd;
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
    private int deletflag;

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

    public String getPdate() {
        return pdate;
    }

    public void setPdate(String pdate) {
        this.pdate = pdate;
    }

    public String getAdate() {
        return adate;
    }

    public void setAdate(String adate) {
        this.adate = adate;
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

    public String getPnum() {
        return pnum;
    }

    public void setPnum(String pnum) {
        this.pnum = pnum;
    }

    public String getAnum() {
        return anum;
    }

    public void setAnum(String anum) {
        this.anum = anum;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBarcodeStart() {
        return barcodeStart;
    }

    public void setBarcodeStart(String barcodeStart) {
        this.barcodeStart = barcodeStart;
    }

    public String getBarcodeEnd() {
        return barcodeEnd;
    }

    public void setBarcodeEnd(String barcodeEnd) {
        this.barcodeEnd = barcodeEnd;
    }

    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public int getDeletflag() {
        return deletflag;
    }

    public void setDeletflag(int deletflag) {
        this.deletflag = deletflag;
    }
}
