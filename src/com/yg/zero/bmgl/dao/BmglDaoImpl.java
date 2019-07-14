package com.yg.zero.bmgl.dao;

import com.yg.zero.bmgl.pojo.Bmgl;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

public class BmglDaoImpl extends SqlMapClientDaoSupport implements BmglDao{
    @Override
    public List<Bmgl> doQueryBmxx() {
        return getSqlMapClientTemplate().queryForList("Bmgl.doQueryBmxx");
    }
}
