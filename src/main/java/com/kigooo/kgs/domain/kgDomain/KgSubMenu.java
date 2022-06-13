package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;

public class KgSubMenu extends KgBasic {
    private String name;
    private int sort;
    private int status;
    private final String type = "sub menu";
    private final String url = null;
    private List<KgMenu> children;

    public KgSubMenu() {

    }

    public KgSubMenu(String oid, long createAt, long updateAt, long deleteAt, int initFlag, String name, int sort, int status, List<KgMenu> children) {
        super(oid, createAt, updateAt, deleteAt, initFlag);
        this.name = name;
        this.sort = sort;
        this.status = status;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public KgSubMenu setName(String name) {
        this.name = name;
        return this;
    }

    public int getSort() {
        return sort;
    }

    public KgSubMenu setSort(int sort) {
        this.sort = sort;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public KgSubMenu setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public List<KgMenu> getChildren() {
        return children;
    }

    public KgSubMenu setChildren(List<KgMenu> children) {
        this.children = children;
        return this;
    }
}
