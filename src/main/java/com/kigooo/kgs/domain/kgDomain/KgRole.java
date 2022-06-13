package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
public class KgRole extends KgBasic {
    private String name;
    private String description;
    private int status;

    public KgRole(){

    }

    public KgRole(String name,String description){
        this.name = name;
        this.description = description;
    }

    public KgRole(String oid, long createAt, long updateAt, long deleteAt, int initFlag, String name, String description, int status) {
        super(oid, createAt, updateAt, deleteAt, initFlag);
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public KgRole setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public KgRole setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public KgRole setStatus(int status) {
        this.status = status;
        return this;
    }
}
