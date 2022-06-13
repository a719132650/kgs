package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
public class KgRelationship {
    private String rid;
    private String fromOID;
    private String toOID;
    private Object fromObj;
    private Object toObj;

    public KgRelationship() {
    }

    public KgRelationship(String rid, String fromOID, String toOID) {
        this.rid = rid;
        this.fromOID = fromOID;
        this.toOID = toOID;
    }

    public KgRelationship(String rid, String fromOID, String toOID, Object fromObj, Object toObj) {
        this.rid = rid;
        this.fromOID = fromOID;
        this.toOID = toOID;
        this.fromObj = fromObj;
        this.toObj = toObj;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getFromOID() {
        return fromOID;
    }

    public void setFromOID(String fromOID) {
        this.fromOID = fromOID;
    }

    public String getToOID() {
        return toOID;
    }

    public void setToOID(String toOID) {
        this.toOID = toOID;
    }

    public Object getFromObj() {
        return fromObj;
    }

    public void setFromObj(Object fromObj) {
        this.fromObj = fromObj;
    }

    public Object getToObj() {
        return toObj;
    }

    public void setToObj(Object toObj) {
        this.toObj = toObj;
    }
}
