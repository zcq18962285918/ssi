package com.yg.zero.ftpFileUpload;

import com.opensymphony.xwork2.ActionSupport;
import net.sf.json.JSONObject;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 大文件上传
 */
public class BigFilesUpload extends ActionSupport {

    private JSONObject result;

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    /**
     * 是否已有此文件上传
     * @return
     */
    public String checkFile(){
        result = new JSONObject();
        HttpServletRequest request = ServletActionContext.getRequest();
        String savePath = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");
        String fileName = request.getParameter("fileName");
        String fileMd5 = request.getParameter("fileMd5");
        String fileSize = request.getParameter("fileSize");
        String fileType = request.getParameter("fileType");



        result.put("success", true);
        //result.put("message", "该文件已上传");
        //result.put("message", "拒绝该文件上传");
        return "success";
    }

    /**
     * 分片是否上传
     * @return
     */
    public String checkChunk(){
        result = new JSONObject();
        HttpServletRequest request = ServletActionContext.getRequest();
        String savePath = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");
        String fileName = request.getParameter("fileName");
        String fileMd5 = request.getParameter("fileMd5");
        int  index = Integer.parseInt(request.getParameter("chunk"));
        long chunkSize = Long.parseLong(request.getParameter("chunkSize"));


        //result.put("ifExist", true);
        result.put("ifExist", false);
        return "success";
    }

    /**
     * 合并分片
     * @return
     */
    public String mergeChunks(){
        result = new JSONObject();
        HttpServletRequest request = ServletActionContext.getRequest();
        String savePath = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");
        result.put("success", true);
        //result.put("message", "合并文件出错了!");
        return "success";
    }

    /**
     * 文件分片上传
     * @return
     */
    public String uploadChunk(){
        HttpServletRequest request = ServletActionContext.getRequest();
        return "success";
    }
}
