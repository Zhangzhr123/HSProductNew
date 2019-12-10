package com.hsproduce.bean;

import java.util.Date;

public class UpdateVersion {

    /** 版本id */
    private Integer versionId;
    /** 版本号 */
    private Integer versionCode;
    /** 版本显示号 */
    private String versionShowCode;
    /** 版本描述 */
    private String versionRemark;
    /** 下载地址 */
    private String downloadPath;
    /** 创建时间 */
    private java.util.Date CreatTimeBegin;
    private java.util.Date CreatTimeEnd;
    private java.util.Date CreatTime;


    private Long startIndex;

    private Long endIndex;

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionShowCode() {
        return versionShowCode;
    }

    public void setVersionShowCode(String versionShowCode) {
        this.versionShowCode = versionShowCode;
    }

    public String getVersionRemark() {
        return versionRemark;
    }

    public void setVersionRemark(String versionRemark) {
        this.versionRemark = versionRemark;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public Date getCreatTimeBegin() {
        return CreatTimeBegin;
    }

    public void setCreatTimeBegin(Date creatTimeBegin) {
        CreatTimeBegin = creatTimeBegin;
    }

    public Date getCreatTimeEnd() {
        return CreatTimeEnd;
    }

    public void setCreatTimeEnd(Date creatTimeEnd) {
        CreatTimeEnd = creatTimeEnd;
    }

    public Date getCreatTime() {
        return CreatTime;
    }

    public void setCreatTime(Date creatTime) {
        CreatTime = creatTime;
    }

    public void setStartIndex(Long startIndex){
        this.startIndex = startIndex;
    }

    public Long getStartIndex(){
        return startIndex;
    }

    public void setEndIndex(Long endIndex){
        this.endIndex = endIndex;
    }

    public Long getEndIndex(){
        return endIndex;
    }
}

