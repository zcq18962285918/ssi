package com.yg.zero.fileUpload.action;

import com.opensymphony.xwork2.ActionSupport;
import com.yg.zero.fileUpload.pojo.FileUploadProgress;
import com.yg.zero.fileUpload.pojo.Files;
import com.yg.zero.fileUpload.service.FileUploadService;
import com.yg.zero.httpFileUpload.SmallFileUpload;
import net.sf.json.JSONObject;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.json.annotations.JSON;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");
        String savePath = path + File.separator + fileMd5;
        File saveFile = new File(savePath);

        //保存文件夹以文件MD5命名
        if (!saveFile.exists() && !saveFile.isDirectory()) {
            if (!saveFile.mkdirs()) {
                System.out.println("创建保存文件夹失败");
            }
        }

        //判断文件是否已存在，不使用文件名判断而是以文件大小和后缀名来进行判断
        File[] listFiles = saveFile.listFiles();
        if (listFiles != null) {
            for (File file1 : listFiles) {
                if (file1.length() == size && !file1.isDirectory()) {
                    String name = file1.getName();
                    String type = name.substring(name.lastIndexOf('.') + 1);
                    if (type.equals(fileType)) {
                        result.put("isWhole", true);
                        return "success";
                    }
                }
            }
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
        String savePath = path + File.separator + fileMd5;

        //分片是否完整存在
        File chunkFile = new File(savePath + File.separator + index);
        if (chunkFile.exists() && !chunkFile.isDirectory()) {
            if (chunkFile.length() == chunkSize) {
                result.put("ifExist", true);
                return "success";
            }
        }
        result.put("ifExist", false);
        return "success";
    }

    /**
     * 合并文件
     *
     * @return
     */
    public String mergeChunks() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String fileName = request.getParameter("fileName");
        String filemd5 = request.getParameter("fileMd5");
        long size = Long.parseLong(request.getParameter("fileSize"));
        String path = ServletActionContext.getServletContext().getRealPath("/WEB-INF/upload");
        String chunksFilePath = path + File.separator + filemd5; //分片文件夹路径
        File mergeFile = new File(chunksFilePath + File.separator + fileName); //合并后的文件
        //文件是否存在，简单的长度判断即可
        if (size == mergeFile.length()) {
            return "success";
        }
        //残缺文件的删除
        if (mergeFile.exists() && !mergeFile.isDirectory())
            if (!mergeFile.delete())
                System.out.println("删除旧文件失败");
        //小于分块大小的文件直接重命名
        if (size <= 10 * 1024 * 1024) {
            File file = new File(chunksFilePath);
            File[] files = file.listFiles();
            if (files == null && files.length > 2) {
                return "error";
            }
            files[0].renameTo(mergeFile);
            return "success";
        }
        //分片文件排序后合并
        File chunksFileDirectory = new File(chunksFilePath);
        File[] files = chunksFileDirectory.listFiles();
        List<File> fileList = null;
        FileChannel outChannel = null;
        FileChannel inChannel = null;
        if (files != null) {
            fileList = new ArrayList<>(Arrays.asList(files));
            fileList.sort((o1, o2) -> {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName()))
                    return -1;
                return 1;
            });

            try {
                mergeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileInputStream fileInputStream = null;
            try {
                outChannel = new FileOutputStream(mergeFile).getChannel();
                for (File file : fileList) {
                    fileInputStream = new FileInputStream(file);
                    inChannel = fileInputStream.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    //file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fileInputStream != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (inChannel != null) {
                inChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (outChannel != null) {
                outChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //检查文件是否合并成功并删除分片文件(程序完成才能删除，删除文件在return之后)
        if (mergeFile.length() != size){
            result.put("error", "文件上传合并失败");
            return "error";
        }

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

}
