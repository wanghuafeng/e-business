package com.rong360.crawler.ds.service.impl;

import com.rong360.crawler.dao.BaseDao;
import com.rong360.crawler.ds.bean.DSOrderCount;
import com.rong360.crawler.ds.service.DSOrderCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dsOrderCountService")
public class DSOrderCountServiceImpl implements DSOrderCountService {
    @Autowired
    private BaseDao baseDao;

    @Override
    public void save(DSOrderCount dsOrderCount) {
        baseDao.save(dsOrderCount);
    }
}
