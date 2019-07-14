package com.yg.zero.fileUpload.service;

import com.yg.zero.fileUpload.dao.FileUploadDao;
import com.yg.zero.fileUpload.pojo.Files;

public class FileUploadServiceImpl implements FileUploadService {
    private FileUploadDao fileUploadDao;

    public FileUploadDao getFileUploadDao() {
        return fileUploadDao;
    }

    public void setFileUploadDao(FileUploadDao fileUploadDao) {
        this.fileUploadDao = fileUploadDao;
    }

    @Override
    public void addFile(Files files) {
        fileUploadDao.addFile(files);
    }

    @Override
    public Files queryByMD5(String filemd5) {
        return fileUploadDao.queryByMD5(filemd5);
    }
}
