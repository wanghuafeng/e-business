package com.rong360.crawler.ds.service.impl;

import com.rong360.crawler.dao.BaseDao;
import com.rong360.crawler.ds.bean.DSApiResult;
import com.rong360.crawler.ds.service.DSApiResultService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("dsApiResultService")
public class DSApiResultServiceImpl implements DSApiResultService {
    @Resource
    private BaseDao baseDao;

    @Override
    public void save(DSApiResult dsApiResult) {
        String hql = "from DSApiResult a where a.uid = ? and a.apiId = ?";
        Object[] object = {dsApiResult.getUid(), dsApiResult.getApiId()};
        List<DSApiResult> list = baseDao.findByHql(hql, DSApiResult.class, object);
        if (list.size() > 0) {
            DSApiResult oldData = list.get(0);
            oldData.merge(dsApiResult);
            baseDao.update(oldData);
        } else {
            baseDao.save(dsApiResult);
        }
    }
}
