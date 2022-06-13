package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;

public class KgMenu extends KgBasic {
    private String name;
    private int sort;
    private int status;
    private final String type = "menu";
    private final String url = null;
    private List<KgCommand> children;
    private String subMenuOid;
    

    public KgMenu() {

    }

    public KgMenu(String oid, long createAt, long updateAt, long deleteAt, int initFlag, String name, int sort, int status, List<KgCommand> children, String subMenuOid) {
        super(oid, createAt, updateAt, deleteAt, initFlag);
        this.name = name;
        this.sort = sort;
        this.status = status;
        this.children = children;
        this.subMenuOid = subMenuOid;
    }

    public String getName() {
        return name;
    }

    public KgMenu setName(String name) {
        this.name = name;
        return this;
    }

    public int getSort() {
        return sort;
    }

    public KgMenu setSort(int sort) {
        this.sort = sort;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public KgMenu setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public List<KgCommand> getChildren() {
        return children;
    }

    public KgMenu setChildren(List<KgCommand> children) {
        this.children = children;
        return this;
    }

    public String getSubMenuOid() {
        return subMenuOid;
    }

    public KgMenu setSubMenuOid(String subMenuOid) {
        this.subMenuOid = subMenuOid;
        return this;
    }
}
