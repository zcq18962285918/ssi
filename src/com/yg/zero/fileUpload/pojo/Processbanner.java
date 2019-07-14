package com.yg.zero.fileUpload.pojo;

public class Processbanner {
    private double upRate = 0.0;//上传速度
    private double percent = 0.01;//上传进度
    private long useTime = 0;//当前耗时量
    private long upSize = 0;//已上传大小
    private long allSize = 0;//文件大小
    private int item;//当前文件索引值

    public double getUpRate() {
        return upRate;
    }

    public void setUpRate(double upRate) {
        this.upRate = upRate;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public long getUpSize() {
        return upSize;
    }

    public void setUpSize(long upSize) {
        this.upSize = upSize;
    }

    public long getAllSize() {
        return allSize;
    }

    public void setAllSize(long allSize) {
        this.allSize = allSize;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }
}
