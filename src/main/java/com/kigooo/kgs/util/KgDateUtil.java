package com.kigooo.kgs.util;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.Date;

public class KgDateUtil {
    /**
     * 日期格式转时间戳
     * @param date 日期
     * @return long
     * @author
     * @date 2021/8/30 15:13
     */
    public static long utcToTimestamp(Date date){
        return date.getTime();
    }
}
