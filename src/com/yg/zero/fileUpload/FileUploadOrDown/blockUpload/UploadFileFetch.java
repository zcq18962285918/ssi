package com.yg.zero.fileUpload.FileUploadOrDown.blockUpload;

import com.yg.zero.fileUpload.FileUploadOrDown.FileUtility;
import com.yg.zero.fileUpload.FileUploadOrDown.MD5;

import java.io.*;

public class UploadFileFetch extends Thread {
    private UploadFileInfoBean uploadFileInfoBean;  //上传的文件信息
    private long[] nStartPos; // 开始位置
    private long[] nEndPos; // 结束位置
    private long nFileLength; //文件长度
    private boolean bFirst = true; // 是否第一次上传文件
    private boolean bStop = false; // 停止标志
    private UploadFileSplitterFetch[] fileSplitterFetch; // 子线程对象
    private File tmpFile; // 文件下载的临时信息
    private int splitter; //分段下载

    /**
     * 上传文件初始化
     *
     * @param bean
     */
    public UploadFileFetch(UploadFileInfoBean bean) {
        uploadFileInfoBean = bean;

        File file = new File(bean.getSavePath());
        if (!file.exists() && !file.isDirectory())
            if (!file.mkdir())
                System.out.println("创建保存文件夹错误");

        // 获取MD5值
        try {
            String md5 = MD5.getFileMD5String(bean.getFile());
            bean.setMd5(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //产生文件信息
        tmpFile = new File(bean.getSavePath() + File.separator + bean.getFileName() + ".info");
        if (tmpFile.exists()) {
            bFirst = false;
            //读取已下载的文件信息
            read_nPos();
        } else {
            /*String[] split = bean.getsFilePath().split("\\\\");
            File file = new File(split[0] + "//" + split[1]);*/
            //临时创建文件夹
            File file1 = new File(bean.getSavePath() + "temp");
            if (!file1.mkdirs())
                System.out.println("已有文件，创建文件失败");
        }
        nStartPos = new long[bean.getnSplitter()];
        nEndPos = new long[bean.getnSplitter()];
        this.splitter = bean.getnSplitter();

        //初始化时设置一个监听器
        //UploadProcessLister uploadProcessLister = new UploadProcessLister();
    }

    public void run() {
        if (bFirst) {
            //获取文件长度
            nFileLength = getFileSize();
            if (nFileLength == -1)
                FileUtility.log("File Length is not known!");
            else if (nFileLength == -2)
                FileUtility.log("File is not access!");
            else {
                // 分割文件
                for (int i = 0; i < nStartPos.length; i++) {
                    nStartPos[i] = i * (nFileLength / nStartPos.length);
                }
                //拷贝函数，性能高于for
                System.arraycopy(nStartPos, 1, nEndPos, 0, nEndPos.length - 1);
                //直到文件结尾
                nEndPos[nEndPos.length - 1] = nFileLength;
            }
        }
        // 启动子线程
        fileSplitterFetch = new UploadFileSplitterFetch[nStartPos.length];
        File file;
        for (int i = 0; i < nStartPos.length; i++) {
            file = new File(File.separator + uploadFileInfoBean.getFileName() + "_" + i);
            try {
                fileSplitterFetch[i] = new UploadFileSplitterFetch(
                        uploadFileInfoBean.getSavePath() + file,
                        nStartPos[i], nEndPos[i], i, bFirst, uploadFileInfoBean.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtility.log("Thread " + i + " , nStartPos = " + nStartPos[i]
                    + ", nEndPos = " + nEndPos[i]);
            fileSplitterFetch[i].run();
        }
        //上传子线程是否完成标志
        boolean breakWhile;
        while (!bStop) {
            write_nPos();
            FileUtility.sleep(500);
            breakWhile = true;
            for (int i = 0; i < nStartPos.length; i++) {
                if (!fileSplitterFetch[i].bUploadOver) {
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

        //合并文件
        String uFile = uploadFileInfoBean.getSavePath() + File.separator + uploadFileInfoBean.getFileName();
        File mergeFile = mergeFile(uFile, splitter);

        String nmd5 = null;
        try {
            //应该是检查合并后的文件
            nmd5 = MD5.getFileMD5String(mergeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean b = MD5.check(uploadFileInfoBean.getMd5(), nmd5);
        if (!b){
            FileUtility.log("文件错误，请重新上传");
            return;
        }
        FileUtility.log("文件合并完成");
    }

    /**
     * 停止上传
     */
    public void siteStop() {
        bStop = true;
        for (int i = 0; i < nStartPos.length; i++)
            fileSplitterFetch[i].splitterStop();
    }

    /**
     * 获取文件大小
     *
     */
    private int getFileSize() {
        try {
            FileInputStream inputStream = new FileInputStream(uploadFileInfoBean.getFile());
            return inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 保存上传信息（文件指针位置）
     */
    private void write_nPos() {
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream(tmpFile));
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
     * 读取已上传文件的信息（文件指针位置）
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
     * 合并文件
     *
     * @param uFile    文件路径
     * @param splitter 文件快数
     */
    private File mergeFile(String uFile, int splitter) {
        FileUtility.log("开始合并文件，请稍等。。。");
        File file = new File(uFile);
        if (file.exists())
            file.delete();
        RandomAccessFile rw = null;
        try {
            rw = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < splitter; i++) {
            File file1 = new File(uFile + "_" + i);
            RandomAccessFile r = null;
            try {
                r = new RandomAccessFile(file1, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] bytes = new byte[1024 * 1024];
            int len;
            try {
                while ((len = r.read(bytes, 0, bytes.length)) > 0) write(rw, bytes, 0, len);
                r.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        if (rw != null) {
            try {
                rw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 写文件
     *
     * @param rw
     * @param b
     * @param nStart
     * @param nLen
     */
    private int write(RandomAccessFile rw, byte[] b, int nStart, int nLen) {
        int n = -1;
        try {
            rw.seek(rw.length());
            rw.write(b, nStart, nLen);
            n = nLen;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

}
