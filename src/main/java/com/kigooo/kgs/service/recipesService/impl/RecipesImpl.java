package com.kigooo.kgs.service.recipesService.impl;

import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.component.minio.MinioUtil;
import com.kigooo.kgs.service.recipesService.RecipesService;
import com.kigooo.kgs.util.KgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class RecipesImpl implements RecipesService {

    @Autowired
    private Environment env;

    @Autowired
    private MinioUtil minioUtil;

    @Override
    public KgResponseJson uploadPic(MultipartFile[] fileList, HttpServletRequest httpServletRequest){
        KgResponseJson kgResponseJson = new KgResponseJson();
        String url = null;
        for(MultipartFile file : fileList){
            try {
                String originName = file.getOriginalFilename();
                String fileType = originName.substring(originName.lastIndexOf(".")+1);
                String preFix = originName.substring(0,originName.lastIndexOf("."));
                String fileName = preFix + "_" + KgUtil.getUUID()+"."+fileType;
                minioUtil.uploadFile(fileName,file);
                url = "/"+env.getProperty("minio.bucketName")+"/"+ fileName;
            } catch (Exception e) {
                log.info("error upload:"+e);
            }
        }
        kgResponseJson.putData("url",url);
        return kgResponseJson;
    }
}
