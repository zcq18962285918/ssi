package com.yg.zero.bmgl.service;

import com.yg.zero.bmgl.dao.BmglDao;
import com.yg.zero.bmgl.pojo.Bmgl;

import java.sql.SQLException;
import java.util.List;

public class BmglServiceImpl implements BmglService{

    private BmglDao bmglDao;

    public BmglDao getBmglDao() {
        return bmglDao;
    }

    public void setBmglDao(BmglDao bmglDao) {
        this.bmglDao = bmglDao;
    }

    @Override
    public List<Bmgl> doQuery() throws SQLException {
        return bmglDao.doQueryBmxx();
    }
}
