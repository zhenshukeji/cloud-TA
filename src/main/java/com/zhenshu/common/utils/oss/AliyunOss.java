package com.zhenshu.common.utils.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.zhenshu.common.utils.file.FileUploadUtils;
import com.zhenshu.framework.config.AliyunConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/6/26 0026 13:57
 **/
@Component
public class AliyunOss implements Storage {
    @Autowired
    private AliyunConfig aliyunConfig;

    @Override
    public Map<String, String> saveFile(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.length());
        // 对象键（Key）是对象在存储桶中的唯一标识
        String keyName = aliyunConfig.getProjectName() + "/" + file.getName();
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliyunConfig.getBucketName(), keyName, fis, objectMetadata);
        getOSSClient().putObject(putObjectRequest);
        // 返回地址
        String url = aliyunConfig.getAddress() + keyName;
        HashMap<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("fileName", keyName);
        return map;
    }

    @Override
    public Map<String, String> saveFile(MultipartFile file) throws IOException {
        // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        // 对象键（Key）是对象在存储桶中的唯一标识
        String keyName = aliyunConfig.getProjectName() + "/" + FileUploadUtils.extractFilename(file);
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliyunConfig.getBucketName(), keyName, file.getInputStream(), objectMetadata);
        getOSSClient().putObject(putObjectRequest);
        // 返回地址
        String url = aliyunConfig.getAddress() + keyName;
        HashMap<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("keyName", keyName);
        return map;
    }

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    private OSSClient getOSSClient() {
        return new OSSClient(aliyunConfig.getEndpoint(), aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret());
    }

    private String getBaseUrl() {
        return aliyunConfig.getAddress() + "/" + aliyunConfig.getProjectName() + "/";
    }


}
