package com.yg.zero.httpFileUpload;

import com.yg.zero.fileUpload.UploadProcessLister;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

/**
 * 小文件上传，不支持断点续传，但是也不需要
 */
public class SmallFileUpload {

    public static void upload(HttpServletRequest request, String savePath) {

        String Md5 = null;
        //临时文件夹和上传文件夹
        String tempPath = savePath + File.separator + "temp";
        File tempDirectory = new File(tempPath);
        if (!tempDirectory.exists() && !tempDirectory.isDirectory())
            if (!tempDirectory.mkdir())
                System.out.println("创建临时文件夹失败");

        //创建DiskFileItemFactory工厂
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
        diskFileItemFactory.setSizeThreshold(1024 * 1024 * 10);
        diskFileItemFactory.setRepository(tempDirectory);

        //创建一个文件上传解析器
        ServletFileUpload fileUpload = new ServletFileUpload(diskFileItemFactory);
        //解决上传文件名的中文乱码
        fileUpload.setHeaderEncoding("UTF-8");

        //设置文件上传的监听器
        fileUpload.setProgressListener(new UploadProcessLister(request));

        //设置上传单个文件的大小的最大值，目前设置为10MB
        fileUpload.setFileSizeMax(1024 * 1024 * 12);
        //设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和
        fileUpload.setSizeMax(1024 * 1024 * 50);

        List<FileItem> fileItemList = null;
        String uploadPath = null;
        String chunk = null;
        try {
            fileItemList = fileUpload.parseRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        if (fileItemList != null && fileItemList.size() > 0) {
            for (FileItem fileItem : fileItemList) {
                if (fileItem.isFormField()) {
                    String fieldName = fileItem.getFieldName();
                    if (fieldName.equals("fileMd5")) {
                        try {
                            Md5 = fileItem.getString("utf-8");
                            //保存文件夹
                            uploadPath = savePath + File.separator + Md5;
                            File saveDirectory = new File(uploadPath);
                            if (!saveDirectory.exists() && !saveDirectory.isDirectory())
                                if (!saveDirectory.mkdirs())
                                    System.out.println("创建上传文件夹失败");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fieldName.equals("chunk")) {
                        try {
                            chunk = fileItem.getString("utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //不同的浏览器提交的文件名称是不一样的，有些浏览器提交的文件会带有路径，如“D:\\project\WebRoot\hello.jsp”，有一些是单纯的文件名：hello.jsp
                    String fileName = fileItem.getName();
                    fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                    //标签名
                    String fieldName = fileItem.getFieldName();
                    System.out.println(fileName + "---" + fieldName);

                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    byte buffer[] = new byte[1024];
                    int len;
                    try {
                        inputStream = fileItem.getInputStream();
                        outputStream = new FileOutputStream(uploadPath + File.separator + chunk);
                        if (inputStream != null) {
                            while ((len = inputStream.read(buffer)) > 0) {
                                //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录当中
                                outputStream.write(buffer, 0, len);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        fileItem.delete();
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除临时文件
     */
    public static void deleteTemp(File tempDirectory, HttpServletRequest request) {

        File[] list = tempDirectory.listFiles();
        if (list != null) {
            for (File file : list)
                if (!file.delete())
                    System.out.println("删除临时文件失败");
        }
        if (!tempDirectory.delete())
            System.out.println("删除临时文件夹失败");
    }
}
