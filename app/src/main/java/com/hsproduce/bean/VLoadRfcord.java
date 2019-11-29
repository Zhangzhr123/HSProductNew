package com.hsproduce.bean;

import java.util.Date;

public class VLoadRfcord {
    //ID号
    private String id;
    //装车单ID号
    private String loadid;
    //装车单行项目ID号
    private String loadhxmid;
    //轮胎条码
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
    //规格编码
    private String itnbr;
    //规格描述
    private String itdsc;
    //装车单号
    private String loadno;
    //装车单行项目
    private String loadnp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoadid() {
        return loadid;
    }

    public void setLoadid(String loadid) {
        this.loadid = loadid;
    }

    public String getLoadhxmid() {
        return loadhxmid;
    }

    public void setLoadhxmid(String loadhxmid) {
        this.loadhxmid = loadhxmid;
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

    public String getLoadno() {
        return loadno;
    }

    public void setLoadno(String loadno) {
        this.loadno = loadno;
    }

    public String getLoadnp() {
        return loadnp;
    }

    public void setLoadnp(String loadnp) {
        this.loadnp = loadnp;
    }
}
