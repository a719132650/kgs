package com.kigooo.kgs.component.kgResponseException;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
public class KgResponseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String code;
    private String msg;

    public KgResponseException(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
