package com.yg.zero.fileUpload.FileUploadOrDown;

public class FileUtility {
    public FileUtility() { }

    /**
     * 休眠时长
     * @param nSecond
     */
    public static void sleep(int nSecond) {
        try {
            Thread.sleep(nSecond);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印日志信息
     * @param sMsg
     */
    public static void log(String sMsg) {
        System.err.println(sMsg);
    }

    /**
     * 打印日志信息
     * @param sMsg
     */
    public static void log(int sMsg) {
        System.err.println(sMsg);
    }
}
