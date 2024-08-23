package com.zhenshu.common.utils.oss;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.zhenshu.common.utils.file.FileUploadUtils;
import com.zhenshu.framework.config.TencentOssConfig;
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
 * 腾讯对象存储服务
 */
@Component
public class TengcentOss implements Storage {

    @Autowired
    private TencentOssConfig ossConfig;

    private COSClient cosClient;

    private COSClient getCOSClient() {
        if (cosClient == null) {
            // 1 初始化用户身份信息(secretId, secretKey)
            COSCredentials cred = new BasicCOSCredentials(ossConfig.getSecretId(), ossConfig.getSecretKey());
            // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
            ClientConfig clientConfig = new ClientConfig(new Region(ossConfig.getRegion()));
            cosClient = new COSClient(cred, clientConfig);
        }
        return cosClient;
    }

    @Override
    public Map<String, String> saveFile(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.length());
        // 对象键（Key）是对象在存储桶中的唯一标识
        String keyName = ossConfig.getProjectName() + "/" + file.getName();
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossConfig.getBucketName(), keyName, fis, objectMetadata);
        getCOSClient().putObject(putObjectRequest);
        // 返回地址
        String url = ossConfig.getAddress() + keyName;
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
        String keyName = ossConfig.getProjectName() + "/" + FileUploadUtils.extractFilename(file);
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossConfig.getBucketName(), keyName, file.getInputStream(), objectMetadata);
        getCOSClient().putObject(putObjectRequest);
        // 返回地址
        String url = ossConfig.getAddress() + keyName;
        HashMap<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("keyName", keyName);
        return map;
    }

}
