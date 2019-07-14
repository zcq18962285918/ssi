package com.yg.zero.fileUpload.FileUploadOrDown;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    /**
     * 默认的密码字符串组合，用来将字节转换成16进制表示的字符
     */
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println(MD5.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
            nsaex.printStackTrace();
        }
    }

    /**
     * 生成字符串的md5校验值
     */
    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    /**
     * 判断字符串的md5校验码是否与一个已知的md5码相匹配
     *
     * @param md5 要校验的字符串
     * @param md5PwdStr 已知的md5校验码
     */
    public static boolean check(String md5, String md5PwdStr) {
        return md5.equals(md5PwdStr);
    }

    /**
     * 生成文件的md5校验值
     *
     */
    public static String getFileMD5String(File file) throws IOException {
        InputStream fis;
        fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int numRead;
        while ((numRead = fis.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, numRead);
        }
        fis.close();
        return bufferToHex(messagedigest.digest());
    }

    /**
     * 生成stream的md5校验值
     *
     */
    public static String getStreamMD5String(InputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        int numRead;
        while ((numRead = stream.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, numRead);
        }
        return bufferToHex(messagedigest.digest());
    }

    /**
     * 生成url的md5校验值
     *
     */
    public static String getURLMD5String(String url) throws IOException {
        URL urlPath = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlPath.openConnection();
        InputStream inputStream = connection.getInputStream();
        byte[] buffer = new byte[1024];
        int numRead;
        while ((numRead = inputStream.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, numRead);
        }
        inputStream.close();
        return bufferToHex(messagedigest.digest());
    }

    private static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>> 为逻辑右移
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
