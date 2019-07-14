package com.yg.zero.fileUpload;

import com.yg.zero.fileUpload.pojo.FileUploadProgress;
import org.apache.commons.fileupload.ProgressListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UploadProcessLister implements ProgressListener {

    private HttpSession session;
    private HttpServletRequest request;
    private double upRate = 0.0;//上传速度
    private double percent = 0.0;//上传进度
    private long useTime = 0;//当前耗时量
    private long upSize = 0;//已上传大小
    private long allSize = 0;//文件大小
    private int item;//当前文件索引值

    private long beginT = System.currentTimeMillis();
    private long curT = System.currentTimeMillis();

    public UploadProcessLister(HttpServletRequest request) {
        session = request.getSession();
        FileUploadProgress fileUploadProgress = new FileUploadProgress();
        fileUploadProgress.setComplete(false);
        session.setAttribute("fileUploadProgress", fileUploadProgress);
    }

    @Override
    public void update(long l, long l1, int i) {//第一个参数：已读入字节数 第二个参数:文件大小(单位byte) 第三个参数:当前文件索引值
        //实现文件上传的核心方法
        Object attribute = session.getAttribute("fileUploadProgress");
        FileUploadProgress fileUploadProgress = null;
        if (attribute == null) {
            fileUploadProgress = new FileUploadProgress();
            fileUploadProgress.setComplete(false);
            session.setAttribute("fileUploadProgress", fileUploadProgress);
        } else {
            fileUploadProgress = (FileUploadProgress) attribute;
        }

        fileUploadProgress.setCurrentLength(l);
        fileUploadProgress.setLength(l1);
        fileUploadProgress.setPercent((int) (l * 100 / l1));
        if (l == l1) {
            fileUploadProgress.setComplete(true);
        } else {
            fileUploadProgress.setComplete(false);
        }

        session.setAttribute("fileUploadProgress", fileUploadProgress);

    }
}
