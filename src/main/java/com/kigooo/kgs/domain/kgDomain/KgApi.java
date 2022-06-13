package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
public class KgApi extends KgBasic {
    private String api;
    private String name;
    private String description;
    private String businessType;
    private int authType;
    private int status;

    public KgApi(){

    }

    public KgApi(String api, String name, String description,String businessType, int authType, int status) {
        this.api = api;
        this.name = name;
        this.description = description;
        this.businessType = businessType;
        this.authType = authType;
        this.status = status;
    }

    public KgApi(String oid, long createAt, long updateAt, long deleteAt, int initFlag, String api, String name, String description,String businessType, int authType, int status) {
        super(oid, createAt, updateAt, deleteAt, initFlag);
        this.api = api;
        this.name = name;
        this.description = description;
        this.businessType = businessType;
        this.authType = authType;
        this.status = status;
    }

    public String getApi() {
        return api;
    }

    public KgApi setApi(String api) {
        this.api = api;
        return this;
    }

    public String getName() {
        return name;
    }

    public KgApi setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public KgApi setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getAuthType() {
        return authType;
    }

    public KgApi setAuthType(int authType) {
        this.authType = authType;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public KgApi setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getBusinessType() {
        return businessType;
    }

    public KgApi setBusinessType(String businessType) {
        this.businessType = businessType;
        return this;
    }
}
