package com.example.common.domain.entity;

import com.example.common.domain.enums.EnterpriseClassification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 这个类不用专门创建数据库，本来就是用来中转的————奶奶滴，没规划好结构
 * @data 10.2
 */
@Data
@Builder
public class Enterprise {
    private Long enterpriseId;
    private String enterpriseName;
    private String country;
    private String province;
    private String city;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String regNo;//注册号码
    private String operName;//法人
    private String enterpriseClassification;
    private List<Position> position;//招聘职位

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
    // Position中也有该方法
    public String getName() {
        return enterpriseName;
    }

    public void setName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEnterpriseClassification() {
        return enterpriseClassification;
    }

    public void setEnterpriseClassification(String enterpriseClassification) {
        this.enterpriseClassification = enterpriseClassification;
    }



    public Enterprise(Long id, String name, String address, String phone, String email, String website, String regNo, String operName, List<Position> position) {
        this.enterpriseId = id;
        this.enterpriseName = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.regNo = regNo;
        this.operName = operName;
        this.position = position;
    }

    public Enterprise(Long enterpriseId, String enterpriseName, String country, String province, String city, String address, String phone, String email, String website, String regNo, String operName, String enterpriseClassification, List<Position> position) {
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.country = country;
        this.province = province;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.regNo = regNo;
        this.operName = operName;
        this.enterpriseClassification = enterpriseClassification;
        this.position = position;
    }

    public Enterprise() {
    }


    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setId(Long id) {
        this.enterpriseId = id;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String name) {
        this.enterpriseName = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public List<Position> getPosition() {
        return position;
    }

    public void setPosition(List<Position> position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Enterprise{" +
                "id=" + enterpriseId +
                ", name='" + enterpriseName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", regNo='" + regNo + '\'' +
                ", operName='" + operName + '\'' +
                ", position=" + position;
    }
}
