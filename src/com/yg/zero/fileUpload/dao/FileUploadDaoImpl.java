package com.yg.zero.fileUpload.dao;

import com.yg.zero.fileUpload.pojo.Files;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class FileUploadDaoImpl extends SqlMapClientDaoSupport implements FileUploadDao {
    @Override
    public void addFile(Files files) {
        getSqlMapClientTemplate().insert("fileUpload.addFile", files);
    }

    @Override
    public Files queryByMD5(String filemd5) {
        return (Files) getSqlMapClientTemplate().queryForObject("fileUpload.queryByMD5", filemd5);
    }
}
