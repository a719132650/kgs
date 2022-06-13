package com.kigooo.kgs.domain.kgDomain;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
public class KgBasic {
    private String oid;
    private long createAt;
    private long updateAt;
    private long deleteAt;
    private int initFlag;

    public KgBasic() {
    }

    public KgBasic(String oid, long createAt, long updateAt, long deleteAt, int initFlag) {
        this.oid = oid;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.deleteAt = deleteAt;
        this.initFlag = initFlag;
    }

    public String getOid() {
        return oid;
    }

    public KgBasic setOid(String oid) {
        this.oid = oid;
        return this;
    }

    public long getCreateAt() {
        return createAt;
    }

    public KgBasic setCreateAt(long createAt) {
        this.createAt = createAt;
        return this;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public KgBasic setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public long getDeleteAt() {
        return deleteAt;
    }

    public KgBasic setDeleteAt(long deleteAt) {
        this.deleteAt = deleteAt;
        return this;
    }

    public int getInitFlag() {
        return initFlag;
    }

    public KgBasic setInitFlag(int initFlag) {
        this.initFlag = initFlag;
        return this;
    }
}
