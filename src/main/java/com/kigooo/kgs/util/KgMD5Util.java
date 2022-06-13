package com.kigooo.kgs.util;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import org.springframework.util.DigestUtils;
import com.kigooo.kgs.config.KgProperties;

public class KgMD5Util {

    private static KgProperties kgProperties = KgSpringUtil.getBean(KgProperties.class);

    /**
     * 字符串MD5加密
     * @param str 字符串
     * @return java.lang.String
     * @author
     * @date 2021/8/30 15:15
     */
    public static String getMD5(String str) {
        String base = str +"/"+kgProperties.getMd5salt();
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
