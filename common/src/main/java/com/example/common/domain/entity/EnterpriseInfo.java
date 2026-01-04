package com.example.common.domain.entity;

public class EnterpriseInfo {
    private String enterpriseName;
    private String url;
    private String beianInfo;
    private String regNo;

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBeianInfo() {
        return beianInfo;
    }

    public void setBeianInfo(String beianInfo) {
        this.beianInfo = beianInfo;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Override
    public String toString() {
        return "EnterpriseInfo{" +
                "enterpriseName='" + enterpriseName + '\'' +
                ", url='" + url + '\'' +
                ", beianInfo='" + beianInfo + '\'' +
                ", regNo='" + regNo + '\'' +
                '}';
    }
}
