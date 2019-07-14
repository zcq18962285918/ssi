package com.yg.zero.fileUpload;

import com.yg.zero.fileUpload.pojo.FileType;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件类型判断
 */
public class FileTypeJudge {
    private FileTypeJudge() {}

    /**
     * 将文件头转换成16进制字符串
     *
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src)
    {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0)
        {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 得到文件头字节块
     *
     * @param is
     *            InputStream 文件输入流
     * @return 16进制文件头
     */
    private static String getFileHead(InputStream is)
    {

        byte[] b = new byte[28];

        try
        {
            is.read(b, 0, 28);
            return bytesToHexString(b);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断文件类型
     *
     * @param is
     *            InputStream 文件输入流
     * @return FileType 文件类型
     */
    public static FileType getType(InputStream is) throws IOException
    {

        String fileHead = getFileHead(is);

        if (fileHead == null || fileHead.length() == 0)
        {
            return null;
        }

        fileHead = fileHead.toUpperCase();

        FileType[] fileTypes = FileType.values();

        for (FileType type : fileTypes)
        {
            if (fileHead.startsWith(type.getValue()))
            {
                return type;
            }
        }

        return null;
    }

    /**
     * 得到文件类型分类码
     *
     * @param value
     *            FileType 文件类型
     * @return Integer 文件类型分类码(图片：1，文档：2，视频：3，种子：4，音乐：5，其他：7)
     */
    public static Integer isFileType(FileType value) {
        Integer type = 7;// 其他
        // 图片
        FileType[] pics = {FileType.JPEG, FileType.PNG, FileType.GIF,
                FileType.TIFF, FileType.BMP, FileType.DWG, FileType.PSD};

        FileType[] docs = {FileType.RTF, FileType.XML, FileType.HTML,
                FileType.CSS, FileType.JS, FileType.EML, FileType.DBX,
                FileType.PST, FileType.XLS_DOC, FileType.XLSX_DOCX,
                FileType.VSD, FileType.MDB, FileType.WPS, FileType.WPD,
                FileType.EPS, FileType.PDF, FileType.QDF, FileType.PWL,
                FileType.ZIP, FileType.RAR, FileType.JSP, FileType.JAVA,
                FileType.CLASS, FileType.JAR, FileType.MF, FileType.EXE,
                FileType.CHM};

        FileType[] videos = {FileType.AVI, FileType.RAM, FileType.RM,
                FileType.MPG, FileType.MOV, FileType.ASF, FileType.MP4,
                FileType.FLV, FileType.MID};

        FileType[] tottents = {FileType.TORRENT};

        FileType[] audios = {FileType.WAV, FileType.MP3};

        FileType[] others = {};

        // 图片
        for (FileType fileType : pics) {
            if (fileType.equals(value)) {
                type = 1;
            }
        }
        // 文档
        for (FileType fileType : docs) {
            if (fileType.equals(value)) {
                type = 2;
            }
        }
        // 视频
        for (FileType fileType : videos) {
            if (fileType.equals(value)) {
                type = 3;
            }
        }
        // 种子
        for (FileType fileType : tottents) {
            if (fileType.equals(value)) {
                type = 4;
            }
        }
        // 音乐
        for (FileType fileType : audios) {
            if (fileType.equals(value)) {
                type = 5;
            }
        }
        return type;
    }
}
