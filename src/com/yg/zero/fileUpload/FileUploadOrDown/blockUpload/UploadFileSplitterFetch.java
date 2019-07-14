package com.yg.zero.fileUpload.FileUploadOrDown.blockUpload;

import com.yg.zero.fileUpload.FileUploadOrDown.FileUtility;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class UploadFileSplitterFetch extends Thread {
    long nStartPos; // 文件分段的开始位置
    long nEndPos; // 文件分段的结束位置
    private int nThreadID; // 线程的 ID
    boolean bUploadOver = false; // 是否上传完成
    private boolean bStop = false; // 停止上传
    private UploadFileAccess fileAccessI = null; // 文件对象
    private File file;//上传的文件
    private boolean bFirst = true;

    /**
     * 上传子线程初始化
     *
     * @param sName
     * @param nStart
     * @param nEnd
     * @param id
     * @throws IOException
     */
    public UploadFileSplitterFetch(String sName, long nStart, long nEnd,
                                   int id, boolean bFirst, File file) throws IOException {
        this.nStartPos = nStart;
        this.nEndPos = nEnd;
        nThreadID = id;
        fileAccessI = new UploadFileAccess(sName, nStartPos, bFirst);
        this.bFirst = bFirst;
        this.file = file;
    }

    public void run() {
        while (nStartPos < nEndPos && !bStop) {
            try {
                RandomAccessFile input = new RandomAccessFile(file, "r");
                input.seek(nStartPos);
                FileUtility.log("Thread " + nThreadID + " is start!");
                byte[] b = new byte[1024 * 1024];
                int nRead;
                while ((nRead = input.read(b, 0, 1024 * 1024)) > 0
                        && nStartPos < nEndPos && !bStop) {
                    if ((nStartPos + nRead) > nEndPos) {
                        nRead = (int) (nEndPos - nStartPos);
                    }
                    nStartPos += fileAccessI.write(b, 0, nRead);
                }
                fileAccessI.oSavedFile.close();
                FileUtility.log("Thread " + nThreadID + " is over!");
                input.close();
                bUploadOver = true;
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!bUploadOver) {
            if (nStartPos >= nEndPos) {
                bUploadOver = true;
            }
        }
    }

    /**
     * 停止
     */
    void splitterStop() {
        bStop = true;
    }
}
