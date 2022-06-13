package com.kigooo.kgs.util;

/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import com.kigooo.kgs.component.kgJWT.KgJWTUtil;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSON;
import com.kigooo.kgs.component.kgResponseException.KgResponseException;
import com.kigooo.kgs.config.KgProperties;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.io.File;
import java.util.*;

@Validated
public class KgUtil {

    private static KgProperties kgProperties = KgSpringUtil.getBean(KgProperties.class);

    public static List<Map<String,String>> uploadFiles(MultipartFile[] file, @Valid @NotBlank String savePath, @Valid @NotBlank String filePrefix, @Valid @Pattern(regexp = "0|1|2") String savePolicy) {
        List<Map<String,String>> mapList = new ArrayList<>();
        for(MultipartFile multipartFile :file){
            Map<String,String> map = new HashMap<>();
            String originalName=multipartFile.getOriginalFilename();
            String suffix = originalName.substring(originalName.lastIndexOf(".") + 1);
            String saveName = filePrefix + getUUID() + "." + suffix;
            String yearFolder = String.valueOf((Calendar.getInstance().get(Calendar.YEAR)));
            String monthFolder = String.valueOf((Calendar.getInstance().get(Calendar.MONTH)+1));
            String visitPath = "/" + saveName;
            if(savePolicy.equals("1")){
                savePath = savePath + "\\" + yearFolder;
                visitPath = "/"+yearFolder+"/"+saveName;
            }
            else if(savePolicy.equals("2")){
                savePath = savePath + "\\" + yearFolder + "\\" + monthFolder;
                visitPath = "/" + yearFolder + "/" + monthFolder + "/" + saveName;
            }
            File newFile=new File(savePath);
            if(!newFile.exists()){
                newFile.mkdirs();
            }
            try{
                multipartFile.transferTo(new File(newFile+"\\" + saveName));
            }catch (Exception e){
                throw new KgResponseException(kgProperties.getCode10500(),"Save file error!");
            }
            map.put("originalName",originalName);
            map.put("saveName",saveName);
            map.put("visitPath",visitPath);
            mapList.add(map);
        }
        return mapList;
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String dealStr(String str){
        return (str==null||str.trim().equals(""))?(""):(str);
    }

    public static boolean isEmpty(Object o){
        if(null==o){
            return true;
        }else{
            if(o instanceof String){
                return o.toString().trim().equals("");
            }else if(o instanceof String[]){
                return !(((String[])o).length > 0);
            }else if(o instanceof Collection){
                return !(((Collection) o).size()>0);
            }else if(o instanceof Map){
                return !(((Map)o).size()>0);
            }else{
                return false;
            }
        }
    }

    public static boolean isNotEmpty(Object o){
        return !isEmpty(o);
    }

    public static Long toLong(String s){
        return Long.parseLong(s);
    }

    public static String toString(Object o){
        return String.valueOf(o);
    }

    public static int toInt(String s){
        return Integer.parseInt(s);
    }

    public static String toJSONString(Object o){
        return JSON.toJSONString(o);
    }

    public static String getUserOIDByHttpRequest(HttpServletRequest httpServletRequest){
        return KgJWTUtil.getUserOIDByToken(httpServletRequest.getHeader(kgProperties.getTokenInHeader()));
    }
}
