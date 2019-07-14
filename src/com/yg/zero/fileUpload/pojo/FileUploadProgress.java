package com.yg.zero.fileUpload.pojo;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class FileUploadProgress implements Serializable {

    private long currentLength;   // 已经上传的字节数，单位：字节
    private long length = 1; // 所有文件的总长度，单位：字节
    private int fileIndex;     // 正在上传第几个文件
    private long startTime;    // 开始上传的时间，用于计算上传速度等
    private int percent;       // 上传百分比
    private long speed;        //当前上传速度
    private long time;         // 上传时间
    private static final SimpleDateFormat SIMPLEFORMAT = new SimpleDateFormat("HH:mm:ss");
    private boolean isComplete = false;// 上传是否完成

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static SimpleDateFormat getSIMPLEFORMAT() {
        return SIMPLEFORMAT;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
