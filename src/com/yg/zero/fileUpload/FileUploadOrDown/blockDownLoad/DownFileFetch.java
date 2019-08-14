package com.yg.zero.fileUpload.FileUploadOrDown.blockDownLoad;

import com.yg.zero.fileUpload.FileUploadOrDown.FileUtility;
import com.yg.zero.fileUpload.FileUploadOrDown.MD5;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownFileFetch extends Thread {
    private DownFileInfoBean siteInfoBean = null; // 文件信息 Bean
    private long[] nStartPos; // 开始位置
    private long[] nEndPos; // 结束位置
    private DownFileSplitterFetch[] fileSplitterFetch; // 子线程对象
    private long nFileLength; // 文件长度
    private boolean bFirst = true; // 是否第一次取文件
    private boolean bStop = false; // 停止标志
    private File tmpFile; // 文件下载的临时信息
    private DataOutputStream output; // 输出到文件的输出流
    private boolean fileflag; //是本地上传还是远程下载的标志
    private File downfile; //本地文件下载
    private int splitter; //分段下载

    /**
     * 下载上传文件抓取初始化
     *
     * @param bean
     */
    public DownFileFetch(DownFileInfoBean bean) {
        siteInfoBean = bean;

        // 获取MD5
        try {
            if (siteInfoBean.isFileflag()) {
                String md5 = MD5.getURLMD5String(siteInfoBean.getsSiteURL());
                siteInfoBean.setMd5(md5);
            }
            else siteInfoBean.setMd5(MD5.getFileMD5String(siteInfoBean.getDownfile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //File.separator windows是\,unix是/
        //文件信息
        tmpFile = new File(bean.getsFilePath() + File.separator + bean.getsFileName() + ".info");
        if (tmpFile.exists()) {
            bFirst = false;
            //读取已下载的文件信息
            read_nPos();
        } else {
            String[] split = bean.getsFilePath().split("\\\\");
            File file = new File(split[0] + "//" + split[1]);
            if (!file.mkdirs())
                System.out.println("已有文件夹，创建临时文件夹失败");
            nStartPos = new long[bean.getnSplitter()];
            nEndPos = new long[bean.getnSplitter()];
        }
        fileflag = bean.isFileflag();
        downfile = bean.getDownfile();
        this.splitter = bean.getnSplitter();
    }

    public void run() {
        // 实例 FileSplitterFetch
        // 启动 FileSplitterFetch 线程
        // 等待子线程返回
        try {
            if (bFirst) {
                // 获得文件长度
                nFileLength = getFileSize();
                if (nFileLength == -1) {
                    FileUtility.log("File Length is not known!");
                } else if (nFileLength == -2) {
                    FileUtility.log("File is not access!");
                } else {
                    // 分割文件
                    for (int i = 0; i < nStartPos.length; i++) {
                        nStartPos[i] = (long) (i * (nFileLength / nStartPos.length));
                    }
                    for (int i = 0; i < nEndPos.length - 1; i++) {
                        nEndPos[i] = nStartPos[i + 1];
                    }
                    //直到文件结尾
                    nEndPos[nEndPos.length - 1] = nFileLength;
                }
            }
            // 启动子线程
            fileSplitterFetch = new DownFileSplitterFetch[nStartPos.length];
            File file;
            for (int i = 0; i < nStartPos.length; i++) {
                file = new File(File.separator + siteInfoBean.getsFileName() + "_" + i);
                fileSplitterFetch[i] = new DownFileSplitterFetch(
                        siteInfoBean.getsSiteURL(), siteInfoBean.getsFilePath() + file,
                        nStartPos[i], nEndPos[i], i, fileflag, downfile, bFirst);
                FileUtility.log("Thread " + i + " , nStartPos = " + nStartPos[i]
                        + ", nEndPos = " + nEndPos[i]);
                fileSplitterFetch[i].run();
            }
            //下载子线程是否完成标志
            boolean breakWhile = false;
            while (!bStop) {
                write_nPos();
                FileUtility.sleep(500);
                breakWhile = true;
                for (int i = 0; i < nStartPos.length; i++) {
                    if (!fileSplitterFetch[i].bDownOver) {
                        breakWhile = false;
                        break;
                    } else {
                        write_nPos();
                    }
                }
                if (breakWhile) {
                    break;
                }
            }
            String sName = siteInfoBean.getsFilePath() + File.separator + siteInfoBean.getsFileName();
            hebinfile(sName, splitter);
            FileUtility.log("文件下载结束！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得文件长度
     *
     * @return
     */
    public long getFileSize() {
        int nFileLength = -1;
        if (fileflag) {
            try {
                URL url = new URL(siteInfoBean.getsSiteURL());
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
                httpConnection.connect();
                int responseCode = httpConnection.getResponseCode();
                if (responseCode >= 400) {
                    processErrorCode(responseCode);
                    //represent access is error
                    return -2;
                }
                String sHeader;
                for (int i = 1; ; i++) {
                    sHeader = httpConnection.getHeaderFieldKey(i);
                    if (sHeader != null) {
                        if (sHeader.equals("Content-Length")) {
                            nFileLength = Integer.parseInt(httpConnection.getHeaderField(sHeader));
                            break;
                        }
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileUtility.log(nFileLength);
        } else {
            try {
                File myflie = downfile;
                nFileLength = (int) myflie.length();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileUtility.log(nFileLength);
        }
        return nFileLength;
    }

    /**
     * 保存下载信息（文件指针位置）
     */
    private void write_nPos() {
        try {
            output = new DataOutputStream(new FileOutputStream(tmpFile));
            output.writeInt(nStartPos.length);
            for (int i = 0; i < nStartPos.length; i++) {
                output.writeLong(fileSplitterFetch[i].nStartPos);
                output.writeLong(fileSplitterFetch[i].nEndPos);
            }
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取保存的下载信息（文件指针位置）
     */
    private void read_nPos() {
        try {
            DataInputStream input = new DataInputStream(new FileInputStream(tmpFile));
            int nCount = input.readInt();
            nStartPos = new long[nCount];
            nEndPos = new long[nCount];
            for (int i = 0; i < nStartPos.length; i++) {
                nStartPos[i] = input.readLong();
                nEndPos[i] = input.readLong();
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出错误信息
     *
     * @param nErrorCode
     */
    private void processErrorCode(int nErrorCode) {
        FileUtility.log("Error Code : " + nErrorCode);
    }

    /**
     * 停止文件下载
     */
    public void siteStop() {
        bStop = true;
        for (int i = 0; i < nStartPos.length; i++)
            fileSplitterFetch[i].splitterStop();
    }

    /**
     * 合并文件
     *
     * @param sName
     * @param splitternum
     */
    private void hebinfile(String sName, int splitternum) {
        try {
            File file = new File(sName);
            if (file.exists()) {
                file.delete();
            }
            RandomAccessFile saveinput = new RandomAccessFile(sName, "rw");
            for (int i = 0; i < splitternum; i++) {
                try {
                    File file1 = new File(sName + "_" + i);
                    RandomAccessFile input = new RandomAccessFile(file1, "r");
                    byte[] b = new byte[1024];
                    int nRead;
                    while ((nRead = input.read(b, 0, 1024)) > 0) {
                        write(saveinput, b, 0, nRead);
                    }
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String nmd5 = MD5.getFileMD5String(file);
            boolean b = MD5.check(siteInfoBean.getMd5(), nmd5);
            if (!b){
                FileUtility.log("文件错误，下载失败");
                return;
            }
            FileUtility.log("file size is " + saveinput.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写文件
     *
     * @param b
     * @param nStart
     * @param nLen
     */
    private int write(RandomAccessFile oSavedFile, byte[] b, int nStart, int nLen) {
        int n = -1;
        try {
            oSavedFile.seek(oSavedFile.length());
            oSavedFile.write(b, nStart, nLen);
            n = nLen;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }
}
