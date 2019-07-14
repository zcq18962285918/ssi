package com.yg.zero.fileUpload.simulate;

import java.io.IOException;

public class ClientToServerTest {
    public static void main(String[] args) {
        FileUpLoadClient fileUpLoadClient = null;
        try {
            fileUpLoadClient = new FileUpLoadClient("127.0.0.1", 10086);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = "C://Users//Administrator//Desktop//01-nosql的简介_.flv";
        if (fileUpLoadClient != null) {
            fileUpLoadClient.sendFile(path);
            fileUpLoadClient.quit();
        }

    }
}
