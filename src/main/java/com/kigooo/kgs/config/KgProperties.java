package com.kigooo.kgs.config;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import lombok.Data;

// 读取自定义配置文件
@Configuration
@PropertySource(value = "classpath:kgPropertis.properties",encoding = "utf-8")
@ConfigurationProperties(prefix = "kgs")
@Component
@Data
public class KgProperties {
    //错误信息
    private String code10001;
    private String msg10001;
    private String code10002;
    private String msg10002;
    private String code10003;
    private String msg10003;
    private String code10004;
    private String msg10004;
    private String code10005;
    private String msg10005;
    private String code10006;
    private String msg10006;
    private String code10500;
    private String msg10500;
    private String code10501;
    private String msg10501;
    
    //token
    private String tokenInHeader;
    private String tokenPrefixInRedis;
    private String tokenDefaultExpire;
    private String tokenSelectKey;

    //MD5加密盐
    private String md5salt;

    //文件路径
    private String userHeaderPath;
}
