package com.yg.zero.fileUpload.dao;

import com.yg.zero.fileUpload.pojo.Files;

public interface FileUploadDao {

    void addFile(Files files);

    Files queryByMD5(String filemd5);
}
