package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
public class KgOperate extends KgBasic {
    private String name;
    private static int sort = 0;
    private int status;
    private final String type = "operate";
    private String url;
    private String description;
    private String commandOid;
    private String apiOid;

    public KgOperate() {

    }

    public KgOperate(String oid, long createAt, long updateAt, long deleteAt, int initFlag, String name, String commandOid, String apiOid, int status, String description, String url) {
        super(oid, createAt, updateAt, deleteAt, initFlag);
        this.name = name;
        this.commandOid = commandOid;
        this.apiOid = apiOid;
        this.status = status;
        this.description = description;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public KgOperate setName(String name) {
        this.name = name;
        return this;
    }

    public static int getSort() {
        return sort;
    }

    public static void setSort(int sort) {
        KgOperate.sort = sort;
    }

    public int getStatus() {
        return status;
    }

    public KgOperate setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public KgOperate setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public KgOperate setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCommandOid() {
        return commandOid;
    }

    public KgOperate setCommandOid(String commandOid) {
        this.commandOid = commandOid;
        return this;
    }

    public String getApiOid() {
        return apiOid;
    }

    public KgOperate setApiOid(String apiOid) {
        this.apiOid = apiOid;
        return this;
    }
}
