package com.kigooo.kgs.component.minio;

import com.kigooo.kgs.util.KgUtil;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MinioUtil {

    @Resource
    private MinioClient minioClient;

    @Autowired
    private Environment env;

    public boolean bucketIsExist(String bucketName) throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    public void makeBucket(String bucketName) throws Exception {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        String bucketPolicy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\"," +
                "\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" +
                bucketName +"\"" +
                "]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" +
                bucketName +
                "/*\"]}]}";
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(bucketPolicy).build());
    }

    public String uploadFile(String bucketName, String objectName, MultipartFile multipartFile) throws Exception {
        return this.uploadFile(bucketName, objectName, multipartFile.getInputStream());
    }

    public String uploadFile(String bucketName, String objectName, InputStream inputStream) throws Exception {
        boolean b = bucketIsExist(bucketName);
        if (!b) {
            this.makeBucket(bucketName);
        }
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(inputStream, inputStream.available(), -1L).build());
        String etag = objectWriteResponse.etag();
        log.info("etag : {}", etag);
        return etag;
    }

    public String uploadFile(String objectName, MultipartFile multipartFile) throws Exception {
        return this.uploadFile(env.getProperty("minio.bucketName"), objectName, multipartFile);
    }

    public String uploadFile(String objectName, InputStream inputStream) throws Exception {
        return this.uploadFile(env.getProperty("minio.bucketName"), objectName, inputStream);
    }

    public InputStream downloadFile(String bucketName, String objectName) throws Exception {
        boolean b = bucketIsExist(bucketName);
        if (!b) {
            this.makeBucket(bucketName);
        }
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    public InputStream downloadFile(String objectName) throws Exception {
        return this.downloadFile(env.getProperty("minio.bucketName"), objectName);
    }

    /**
     * 获取分享链接
     *
     * @param bucketName 桶名称
     * @param objectName 文件名称
     * @return 文件直链
     */
    public String getShareUrl(String bucketName, String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).object(objectName).expiry(1, TimeUnit.DAYS).build());
    }

    public String getShareUrl(String objectName) throws Exception {
        return this.getShareUrl(env.getProperty("minio.bucketName"), objectName);
    }


}
