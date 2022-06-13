package com.kigooo.kgs.component.kgResponseException;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.config.KgProperties;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@ControllerAdvice
public class KgExceptionGlobalDeal {
    @Autowired
    private KgProperties kgProperties;

    private static final Logger logger = LoggerFactory.getLogger("APP_LOG");

    /**
     * Exception全局异常处理
     * @param e 捕获异常
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 13:22
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public KgResponseJson exceptionHandler(Exception e){
        logger.error(getExceptionInfo(e));
        KgResponseJson kgResponseJson = new KgResponseJson(kgProperties.getCode10500(),e.getMessage());
        return kgResponseJson;
    }

    /**
     * RuntimeException全局异常处理
     * @param e 捕获异常
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 13:23
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public KgResponseJson runtimeExceptionHandler(RuntimeException e){
        logger.error(getExceptionInfo(e));
        KgResponseJson kgResponseJson = new KgResponseJson(kgProperties.getCode10501(),e.getMessage());
        return kgResponseJson;
    }

    /**
     * ConstraintViolationException验证错误
     * @param e 捕获异常
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 13:23
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public KgResponseJson constraintViolationExceptionHandler(ConstraintViolationException e){
        logger.error(e.getMessage());
        KgResponseJson kgResponseJson = new KgResponseJson(kgProperties.getCode10004(),kgProperties.getMsg10004() +", "+e.getMessage());
        return kgResponseJson;
    }

    /**
     * 自定义异常KgResponseException全局处理
     * @param e 捕获异常
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 13:25
     */
    @ExceptionHandler(value = KgResponseException.class)
    @ResponseBody
    public KgResponseJson kgResponseExceptionHandler(KgResponseException e){
        logger.error(e.getMsg());
        KgResponseJson kgResponseJson = new KgResponseJson(e.getCode(),e.getMsg());
        return kgResponseJson;
    }

    private static String getExceptionInfo(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        ex.printStackTrace(printStream);
        String rs = new String(out.toByteArray());
        try {
            printStream.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }
}
