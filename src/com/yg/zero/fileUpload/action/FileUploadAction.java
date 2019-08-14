package com.yg.zero.fileUpload.action;

import com.opensymphony.xwork2.ActionSupport;
import com.yg.zero.fileUpload.FileUploadOrDown.blockDownLoad.DownFileFetch;
import com.yg.zero.fileUpload.FileUploadOrDown.blockDownLoad.DownFileInfoBean;
import com.yg.zero.fileUpload.pojo.FileUploadProgress;
import com.yg.zero.fileUpload.pojo.Files;
import com.yg.zero.fileUpload.service.FileUploadService;
import com.yg.zero.fileUploads.SmallFileUpload;
import net.sf.json.JSONObject;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.json.annotations.JSON;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

public class FileUploadAction extends ActionSupport {

    private JSONObject result;

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    private FileUploadService fileUploadService;

    @JSON(serialize = false)
    public FileUploadService getFileUploadService() {
        return fileUploadService;
    }

    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    public String upload() {

        result = new JSONObject();
        HttpServletRequest request = ServletActionContext.getRequest();
        long available = 0;
        try {
            available = request.getInputStream().available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String savePath = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");

        if (available < 1024 * 1024 * 100) {
            SmallFileUpload.upload(request, savePath);
        }

        Files file = new Files();
        //todo 保存数据库

        /*保存数据库
        Files files = new Files();
        files.setId(UUID.randomUUID().toString().substring(0, 10));
        files.setFileName(fileFileName);
        files.setFilePath(savePath + "\\" + fileFileName);
        files.setFileSize(BigDecimal.valueOf(file.length()));
        files.setFileSuffix(fileFileName.substring(fileFileName.lastIndexOf(".") + 1));
        files.setSaveTime(new Date());
        fileUploadService.addFile(files);*/

        result.put("info", "文件上传成功");
        return "success";
    }

    /**
     * 分片上传等价于一个小文件的上传
     */
    public void uploadChunk() {
        result = new JSONObject();
        HttpServletRequest request = ServletActionContext.getRequest();
        String savePath = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");
        SmallFileUpload.upload(request, savePath);
    }

    /**
     * 文件上传之前的效检，存在文件则为秒传
     */
    public String checkFile() {
        result = new JSONObject();
        HttpServletRequest request = ServletActionContext.getRequest();
        String fileName = request.getParameter("fileName");
        long size = Long.parseLong(request.getParameter("fileSize"));
        String fileMd5 = request.getParameter("fileMd5");
        String path = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");

        if (SmallFileUpload.checkFile(fileName, size, fileMd5, path)){
            result.put("isWhole", true);
            return "success";
        }

        return "success";
    }

    /**
     * 分片上传之前的效检
     */
    public String checkChunk() {
        result = new JSONObject();
        HttpServletRequest request = ServletActionContext.getRequest();
        String fileMd5 = request.getParameter("fileMd5");
        int index = Integer.parseInt(request.getParameter("chunk"));
        long chunkSize = Long.parseLong(request.getParameter("chunkSize"));
        String path = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");

        if (SmallFileUpload.checkChunk(fileMd5, index, chunkSize, path)){
            result.put("ifExist", true);
            return "success";
        }
        result.put("ifExist", false);
        return "success";
    }

    /**
     * 合并文件
     *
     */
    public String mergeChunks() {
        result = new JSONObject();
        HttpServletRequest request = ServletActionContext.getRequest();
        String fileName = request.getParameter("fileName");
        String filemd5 = request.getParameter("fileMd5");
        long size = Long.parseLong(request.getParameter("fileSize"));
        String path = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");

        if (!SmallFileUpload.mergeChunks(fileName, filemd5, size, path)){
            result.put("error", true);
            return "success";
        }

        //todo 录入数据库

        return "success";
    }

    /**
     * 进度信息
     *
     * @return
     */
    public String progress() {

        result = new JSONObject();
        // 新建当前上传文件的进度信息对象
        FileUploadProgress p;
        Object attribute = ServletActionContext.getRequest().getSession().getAttribute("fileUploadProgress");
        if (null == attribute) {
            p = new FileUploadProgress();
            // 缓存progress对象
            ServletActionContext.getRequest().getSession().setAttribute("fileUploadProgress", p);
        } else {
            p = (FileUploadProgress) attribute;
        }

        ServletActionContext.getResponse().setContentType("text/html;charset=UTF-8");
        ServletActionContext.getResponse().setHeader("pragma", "no-cache");
        ServletActionContext.getResponse().setHeader("cache-control", "no-cache");
        ServletActionContext.getResponse().setHeader("expires", "0");

        result.put("progressLister", p);

        return "success";
    }

    /**
     * 清除session
     */
    public void clearProgressSession() {
        ServletActionContext.getRequest().getSession().setAttribute("fileUploadProgress", null);
    }

    /**
     * 文件下载
     */
    public void download(){
        HttpServletRequest request = ServletActionContext.getRequest();
        String fileName = request.getParameter("fileName");
        String path = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");
        path = path + File.separator + "b79c224a2da4c1c6ab35e635efc31e9e" + File.separator + fileName;

        String savePath = "E:\\temp";

      /*  DownFileInfoBean bean = new DownFileInfoBean(
                path,
                savePath,
                fileName, 1, true, null);*/

        DownFileInfoBean bean = new DownFileInfoBean(
                null,
                "E:\\temp",
                fileName, 1, false,
                new File(path));

        DownFileFetch fileFetch = new DownFileFetch(bean);
        fileFetch.run();
    }

}
