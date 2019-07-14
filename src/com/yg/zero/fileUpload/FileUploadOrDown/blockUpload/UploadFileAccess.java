package com.yg.zero.fileUpload.FileUploadOrDown.blockUpload;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class UploadFileAccess implements Serializable {

    private static final long serialVersionUID = -2518013155671212866L;

    RandomAccessFile oSavedFile; //写入文件的流
    private long nPos; //开始位置
    private boolean bFirst; //是否第一次上传

    /**
     * 默认初始化
     */
    public UploadFileAccess() throws IOException{
        this("", 0, true);
    }

    /**
     * 写入文件初始化
     *
     * @param sName
     * @param nPos
     * @throws IOException
     */
    public UploadFileAccess(String sName, long nPos, boolean bFirst) throws IOException {
        File wfile = new File(sName);
        oSavedFile = new RandomAccessFile(wfile, "rw");
        if (!bFirst) {
            oSavedFile.seek(wfile.length());
        }
        this.nPos = nPos;
        this.bFirst = bFirst;
    }

    /**
     * 写文件
     *
     * @param b
     * @param nStart
     * @param nLen
     * @return
     */
    public synchronized int write(byte[] b, int nStart, int nLen) {
        int n = -1;
        try {
            oSavedFile.write(b, nStart, nLen);
            n = nLen;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }
}
