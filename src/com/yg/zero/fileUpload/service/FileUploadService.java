package com.yg.zero.fileUpload.service;

import com.yg.zero.fileUpload.pojo.Files;

public interface FileUploadService {
    void addFile(Files files);

    Files queryByMD5(String filemd5);
}
