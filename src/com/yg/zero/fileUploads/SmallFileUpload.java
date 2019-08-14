package com.yg.zero.fileUploads;

import com.yg.zero.fileUpload.UploadProcessLister;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传，断点续传
 */
public class SmallFileUpload {

    public static void upload(HttpServletRequest request, String savePath) {

        String Md5;
        //临时文件夹和上传文件夹
        String tempPath = savePath + File.separator + "temp";
        File tempDirectory = new File(tempPath);
        if (!tempDirectory.exists() && !tempDirectory.isDirectory())
            if (!tempDirectory.mkdir())
                System.out.println("创建临时文件夹失败");

        //创建DiskFileItemFactory工厂
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
        diskFileItemFactory.setSizeThreshold(1024 * 1024 * 5);
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
                    } finally {
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
     * 删除临时文件 找不到是哪个临时文件，文件夹可能有别的用户使用不能直接删除，删除现如今想到两个办法，通过时间，定时清理
     */
    public static void deleteTemp(File tempDirectory, HttpServletRequest request) {

    }

    /**
     * 检查文件是否完整存在
     */
    public static boolean checkFile(String fileName, long size, String fileMd5, String path) {
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
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
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 断点续传方法，分片上传之前的效检
     */
    public static boolean checkChunk(String fileMd5, int index, long chunkSize, String path) {
        String savePath = path + File.separator + fileMd5;

        //分片是否完整存在
        File chunkFile = new File(savePath + File.separator + index);
        if (chunkFile.exists() && !chunkFile.isDirectory()) {
            return chunkFile.length() == chunkSize;
        }
        return false;
    }

    /**
     * 断点续传方法，合并分片
     */
    public static boolean mergeChunks(String fileName, String filemd5, long size, String path) {
        String chunksFilePath = path + File.separator + filemd5; //分片文件夹路径
        File mergeFile = new File(chunksFilePath + File.separator + fileName); //合并后的文件
        //文件是否存在，简单的长度判断即可
        if (size == mergeFile.length()) {
            return true;
        }
        //残缺文件的删除,好像没必要
        if (mergeFile.exists() && !mergeFile.isDirectory())
            if (!mergeFile.delete())
                System.out.println("删除残缺文件失败");
        //小于分块大小的文件直接重命名
        if (size <= 10 * 1024 * 1024) {
            File file = new File(chunksFilePath);
            File[] files = file.listFiles();
            if (files == null || files.length > 1) {
                return false;
            }
            files[0].renameTo(mergeFile);
            return true;
        }

        //分片文件排序后合并
        File chunksFileDirectory = new File(chunksFilePath);
        File[] files = chunksFileDirectory.listFiles();
        List<File> fileList;
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
            System.out.println("文件上传合并文件失败");
            return false;
        }
        return true;
    }
}
