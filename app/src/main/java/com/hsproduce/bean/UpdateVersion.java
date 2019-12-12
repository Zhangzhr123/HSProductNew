package com.hsproduce.bean;

import java.util.Date;

public class UpdateVersion {

    /** 版本id */
    private String itemid;
    /** 版本号 */
    private String itemname;
    /** 下载地址 */
    private String downloadPath;

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}

