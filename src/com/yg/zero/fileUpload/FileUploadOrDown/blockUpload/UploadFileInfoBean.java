package com.yg.zero.fileUpload.FileUploadOrDown.blockUpload;

import java.io.File;

public class UploadFileInfoBean {
    private File file; //上传的文件
    private String fileName; //上传的文件名称
    private String savePath; //上传的文件的保存路径
    private String md5;  //文件md5值
    private int nSplitter = 5; // 文件分成几段，默认是5段

    public UploadFileInfoBean(File file, String fileFileName, String savePath, String s, int i) {
        this.file = file;
        this.fileName = fileFileName;
        this.savePath = savePath;
        this.md5 = s;
        this.nSplitter = i;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getnSplitter() {
        return nSplitter;
    }

    public void setnSplitter(int nSplitter) {
        this.nSplitter = nSplitter;
    }
}
