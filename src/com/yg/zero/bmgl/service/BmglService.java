package com.yg.zero.bmgl.service;

import com.yg.zero.bmgl.pojo.Bmgl;

import java.sql.SQLException;
import java.util.List;

public interface BmglService {
    List<Bmgl> doQuery() throws SQLException;
}
