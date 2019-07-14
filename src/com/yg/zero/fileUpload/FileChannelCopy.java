package com.yg.zero.fileUpload;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileChannelCopy {

    //channel复制
    private static void fileChannelCopy(String sourceFilePath, String targetFilePath){
        FileChannel fin = null;
        FileChannel fon = null;
        try {
            fin = new FileInputStream(sourceFilePath).getChannel();
            fon = new FileOutputStream(targetFilePath).getChannel();
            fin.transferTo(0, fin.size(), fon);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fon != null) {
                try {
                    fon.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
