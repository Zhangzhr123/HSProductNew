package com.hsproduce.bean;

public class AppVersion {


    /** 版本id */
    private Integer 版本id;
    /** 版本号 */
    private Integer 版本号;
    /** 版本显示号 */
    private String 版本显示号;
    /** 版本描述 */
    private String 版本描述;
    /** 下载地址 */
    private String 下载地址;
    /** 创建时间 */
    private java.util.Date 创建时间Begin;
    private java.util.Date 创建时间End;
    private java.util.Date 创建时间;


    private Long startIndex;

    private Long endIndex;
    public Integer get版本id() {
        return this.版本id;
    }

    public void set版本id(Integer value) {
        this.版本id = value;
    }

    public Integer get版本号() {
        return this.版本号;
    }

    public void set版本号(Integer value) {
        this.版本号 = value;
    }

    public String get版本显示号() {
        return this.版本显示号;
    }

    public void set版本显示号(String value) {
        this.版本显示号 = value;
    }

    public String get版本描述() {
        return this.版本描述;
    }

    public void set版本描述(String value) {
        this.版本描述 = value;
    }

    public String get下载地址() {
        return this.下载地址;
    }

    public void set下载地址(String value) {
        this.下载地址 = value;
    }

    public java.util.Date get创建时间Begin() {
        return this.创建时间Begin;
    }

    public void set创建时间Begin(java.util.Date value) {
        this.创建时间Begin = value;
    }

    public java.util.Date get创建时间End() {
        return this.创建时间End;
    }

    public void set创建时间End(java.util.Date value) {
        this.创建时间End = value;
    }

    public java.util.Date get创建时间() {
        return this.创建时间;
    }

    public void set创建时间(java.util.Date value) {
        this.创建时间 = value;
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

