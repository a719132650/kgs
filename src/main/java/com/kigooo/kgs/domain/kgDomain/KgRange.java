package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
public class KgRange extends KgBasic {
    private String dataMode;
    private String rangeId;
    private String id;
    private String label;
    private String description;
    private String ctl1;
    private String ctl2;
    private String ctl3;
    private int status;

    public KgRange(){

    }

    public KgRange(String rangeId, String id, String label, String description, String ctl1, String ctl2, String ctl3, int status) {
        this.rangeId = rangeId;
        this.id = id;
        this.label = label;
        this.description = description;
        this.ctl1 = ctl1;
        this.ctl2 = ctl2;
        this.ctl3 = ctl3;
        this.status = status;
    }

    public KgRange(String oid, long createAt, long updateAt, long deleteAt, int initFlag,String dataMode, String rangeId, String id, String label, String description, String ctl1, String ctl2, String ctl3, int status) {
        super(oid, createAt, updateAt, deleteAt, initFlag);
        this.dataMode = dataMode;
        this.rangeId = rangeId;
        this.id = id;
        this.label = label;
        this.description = description;
        this.ctl1 = ctl1;
        this.ctl2 = ctl2;
        this.ctl3 = ctl3;
        this.status = status;
    }

    public String getDataMode() {
        return dataMode;
    }

    public KgRange setDataMode(String dataMode) {
        this.dataMode = dataMode;
        return this;
    }

    public String getId() {
        return id;
    }

    public KgRange setId(String id) {
        this.id = id;
        return this;
    }

    public String getRangeId() {
        return rangeId;
    }

    public KgRange setRangeId(String rangeId) {
        this.rangeId = rangeId;
        return this;
    }
    
    public String getLabel() {
        return label;
    }

    public KgRange setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public KgRange setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCtl1() {
        return ctl1;
    }

    public KgRange setCtl1(String ctl1) {
        this.ctl1 = ctl1;
        return this;
    }

    public String getCtl2() {
        return ctl2;
    }

    public KgRange setCtl2(String ctl2) {
        this.ctl2 = ctl2;
        return this;
    }

    public String getCtl3() {
        return ctl3;
    }

    public KgRange setCtl3(String ctl3) {
        this.ctl3 = ctl3;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public KgRange setStatus(int status) {
        this.status = status;
        return this;
    }
}
