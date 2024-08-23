package com.zhenshu.common.utils.oss;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/6/29 0029 11:06
 **/
public interface Storage {

    Map<String, String> saveFile(File file) throws FileNotFoundException;

    Map<String, String> saveFile(MultipartFile file) throws IOException;
}
