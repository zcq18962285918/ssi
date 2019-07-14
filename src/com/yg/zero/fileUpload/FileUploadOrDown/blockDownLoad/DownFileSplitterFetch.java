package com.yg.zero.fileUpload.FileUploadOrDown.blockDownLoad;

import com.yg.zero.fileUpload.FileUploadOrDown.FileUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownFileSplitterFetch extends Thread {
    private String sURL; // 下载文件的地址
    long nStartPos; // 文件分段的开始位置
    long nEndPos; // 文件分段的结束位置
    private int nThreadID; // 线程的 ID
    boolean bDownOver = false; // 是否下载完成
    private boolean bStop = false; // 停止下载
    private DownFileAccess fileAccessI = null; // 文件对象
    private boolean fileflag; //是URL下载还是本地下载
    private File file = null;//本地下载文件
    private boolean bFirst = true;

    /**
     * 下载，上传子线程初始化
     *
     * @param sURL
     * @param sName
     * @param nStart
     * @param nEnd
     * @param id
     * @param fileflag
     * @param downfile
     * @throws IOException
     */
    public DownFileSplitterFetch(String sURL, String sName, long nStart, long nEnd,
                                 int id, boolean fileflag, File downfile, boolean bFirst) throws IOException {
        this.sURL = sURL;
        this.nStartPos = nStart;
        this.nEndPos = nEnd;
        nThreadID = id;
        fileAccessI = new DownFileAccess(sName, nStartPos, bFirst);
        this.fileflag = fileflag;
        this.file = downfile;
        this.bFirst = bFirst;
    }

    /**
     * 线程执行
     */
    public void run() {
        if (fileflag) {
            this.urldownload();
        } else {
            this.filedownload();
        }
    }

    /**
     * 打印回应的头信息
     *
     * @param con
     */
    public void logResponseHead(HttpURLConnection con) {
        for (int i = 1; ; i++) {
            String header = con.getHeaderFieldKey(i);
            if (header != null) {
                FileUtility.log(header + " : " + con.getHeaderField(header));
            } else {
                break;
            }
        }
    }

    /**
     * 地址下载
     */
    private void urldownload() {
        FileUtility.log("Thread " + nThreadID + " url down filesize is " + (nEndPos - nStartPos));
        FileUtility.log("Thread " + nThreadID + " url start >> " + nStartPos + "------end >> " + nEndPos);
        while (nStartPos < nEndPos && !bStop) {
            try {
                URL url = new URL(sURL);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

                setHeader(httpConnection);

                //防止网络阻塞，设置指定的超时时间；单位都是ms。超过指定时间，就会抛出异常
                httpConnection.setReadTimeout(20000);//读取数据的超时设置
                httpConnection.setConnectTimeout(20000);//连接的超时设置

                //向服务器请求指定区间段的数据，这是实现断点续传的根本。
                String sProperty = "bytes=" + nStartPos + "-";
                httpConnection.setRequestProperty("RANGE", sProperty);
                FileUtility.log(sProperty);

                InputStream input = httpConnection.getInputStream();
                byte[] b = new byte[1024];
                int nRead;
                while ((nRead = input.read(b, 0, 1024)) > 0
                        && nStartPos < nEndPos && !bStop) {
                    if ((nStartPos + nRead) > nEndPos) {
                        nRead = (int) (nEndPos - nStartPos);
                    }
                    nStartPos += fileAccessI.write(b, 0, nRead);
                }
                FileUtility.log("Thread " + nThreadID + " nStartPos : " + nStartPos);
                fileAccessI.oSavedFile.close();
                FileUtility.log("Thread " + nThreadID + " is over!");
                input.close();
                bDownOver = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!bDownOver) {
            if (nStartPos >= nEndPos) {
                bDownOver = true;
            }
        }
    }

    /*
     * 为一个HttpURLConnection模拟请求头，伪装成一个浏览器发出的请求
     */
    private void setHeader(HttpURLConnection httpConnection) {
        httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
    }

    /**
     * 文件下载
     */
    private void filedownload() {
        FileUtility.log("Thread " + nThreadID + " down filesize is " + (nEndPos - nStartPos));
        FileUtility.log("Thread " + nThreadID + " start >> " + nStartPos + "------end >> " + nEndPos);
        while (nStartPos < nEndPos && !bStop) {
            try {
                RandomAccessFile input = new RandomAccessFile(file, "r");
                input.seek(nStartPos);
                byte[] b = new byte[1024];
                int nRead;
                while ((nRead = input.read(b, 0, 1024)) > 0
                        && nStartPos < nEndPos && !bStop) {
                    if ((nStartPos + nRead) > nEndPos) {
                        nRead = (int) (nEndPos - nStartPos);
                    }
                    nStartPos += fileAccessI.write(b, 0, nRead);
                }
                fileAccessI.oSavedFile.close();
                FileUtility.log("Thread " + nThreadID + " is over!");
                input.close();
                bDownOver = true;
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!bDownOver) {
            if (nStartPos >= nEndPos) {
                bDownOver = true;
            }
        }
        FileUtility.log("Thread " + nThreadID + "last start >> " + nStartPos);
    }

    /**
     * 停止
     */
    void splitterStop() {
        bStop = true;
    }
}
