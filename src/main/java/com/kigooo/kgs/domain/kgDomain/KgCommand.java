package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;

public class KgCommand extends KgBasic {
    private String name;
    private int sort;
    private int status;
    private final String type = "command";
    private String url;
    private String menuOid;
    private List<KgOperate> children;
    private List<KgOperate> operateList;
    private List<KgApi> apiList;
    
    public KgCommand() {
        
    }

    public KgCommand(String oid, long createAt, long updateAt, long deleteAt, int initFlag, String name, int sort, int status, String url, String menuOid, List<KgApi> apiList, List<KgOperate> operateList) {
        super(oid, createAt, updateAt, deleteAt, initFlag);
        this.name = name;
        this.sort = sort;
        this.status = status;
        this.url = url;
        this.menuOid = menuOid;
        this.apiList = apiList;
        this.operateList = operateList;
        this.children = operateList;
    }

    public String getName() {
        return name;
    }

    public KgCommand setName(String name) {
        this.name = name;
        return this;
    }

    public int getSort() {
        return sort;
    }

    public KgCommand setSort(int sort) {
        this.sort = sort;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public KgCommand setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public KgCommand setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getMenuOid() {
        return menuOid;
    }

    public KgCommand setMenuOid(String menuOid) {
        this.menuOid = menuOid;
        return this;
    }

    public List<KgOperate> getChildren() {
        return children;
    }

    public KgCommand setChildren(List<KgOperate> children) {
        this.children = children;
        return this;
    }

    public List<KgOperate> getOperateList() {
        return operateList;
    }

    public KgCommand setOperateList(List<KgOperate> operateList) {
        this.operateList = operateList;
        return this;
    }

    public List<KgApi> getApiList() {
        return apiList;
    }

    public KgCommand setApiList(List<KgApi> apiList) {
        this.apiList = apiList;
        return this;
    }
}
