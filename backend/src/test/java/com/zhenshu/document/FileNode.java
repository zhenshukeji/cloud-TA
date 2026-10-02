package com.zhenshu.document;

import java.util.ArrayList;

/**
 * @author jing
 * @version 1.0
 * @desc 文件节点
 * @date 2021/9/26 0026 18:51
 **/
public class FileNode {
    private String fileName;//文件名
    private ArrayList<FileNode> subFiles;//如果是文件，则值为null，如果是文件夹，则每个元素代表该文件夹下的文件(夹)名

    public FileNode(String fileName){
        this.fileName=fileName;
        this.subFiles=null;
    }

    public String getFileName() {
        return fileName;
    }

    public ArrayList<FileNode> getSubFiles() {
        return subFiles;
    }

    public void setSubFiles(ArrayList<FileNode> subFiles) {
        this.subFiles = subFiles;
    }
}
