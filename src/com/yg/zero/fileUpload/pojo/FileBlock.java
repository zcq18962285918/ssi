package com.yg.zero.fileUpload.pojo;

import java.sql.Blob;

public class FileBlock {
    private Long start;
    private Long end;
    private Blob blob;

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Blob getBlob() {
        return blob;
    }

    public void setBlob(Blob blob) {
        this.blob = blob;
    }
}
