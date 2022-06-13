package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
public class KgUser extends KgBasic {
    private String uAccount;
    private String uPassword;
    private String uName;
    private String phone;
    private String headUrl;
    private String sex;
    private long lastLoginAt;
    private int status;

    public KgUser(){

    }

    public KgUser(String oid, long createAt, long updateAt, long deleteAt, int initFlag, String uAccount, String uPassword, String uName, String phone, String headUrl, String sex, long lastLoginAt, int status) {
        super(oid, createAt, updateAt, deleteAt, initFlag);
        this.uAccount = uAccount;
        this.uPassword = uPassword;
        this.uName = uName;
        this.phone = phone;
        this.headUrl = headUrl;
        this.sex = sex;
        this.lastLoginAt = lastLoginAt;
        this.status = status;
    }

    public KgUser(String uAccount, String uPassword) {
        this.uAccount = uAccount;
        this.uPassword = uPassword;
    }

    public String getuAccount() {
        return uAccount;
    }

    public KgUser setuAccount(String uAccount) {
        this.uAccount = uAccount;
        return this;
    }

    public String getuPassword() {
        return uPassword;
    }

    public KgUser setuPassword(String uPassword) {
        this.uPassword = uPassword;
        return this;
    }

    public String getuName() {
        return uName;
    }

    public KgUser setuName(String uName) {
        this.uName = uName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public KgUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public KgUser setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public KgUser setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public long getLastLoginAt() {
        return lastLoginAt;
    }

    public KgUser setLastLoginAt(long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public KgUser setStatus(int status) {
        this.status = status;
        return this;
    }
}
