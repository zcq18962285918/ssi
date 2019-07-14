package com.yg.zero.fileUpload.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class Files {
    private String id;//文件id
    private String fileName;//文件名称
    private BigDecimal fileSize;//文件大小
    private Date saveTime;//文件保存时间
    private String filePath;//文件路径
    private String md5;//文件md5
    private Integer info;//是文件还是文件信息的标志，0是文件,1是文件信息

    public Integer getInfo() {
        return info;
    }

    public void setInfo(Integer info) {
        this.info = info;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BigDecimal getFileSize() {
        return fileSize;
    }

    public void setFileSize(BigDecimal fileSize) {
        this.fileSize = fileSize;
    }

    public Date getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(Date saveTime) {
        this.saveTime = saveTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
