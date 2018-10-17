package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.transing.dpmbs.biz.service.ParamService;
import com.transing.dpmbs.integration.ParamDataService;
import com.transing.dpmbs.integration.bo.ParamBO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author lanceyan
 * @version 1.0
 */
@Service("paramServicePojo")
public class ParamServicePojo extends BaseService implements ParamService {

    @Resource
    private ParamDataService paramDataService;

    @Override
    public List<ParamBO> getKeyValueListByType(List<String> typeList) throws BizException {
        try {
            return paramDataService.getKeyValueListByType(typeList);
        } catch (DAOException e) {
            throw new BizException(e);
        } finally {
        }
    }

    @Override
    public List<ParamBO> getParamBoListByType(String type) throws BizException {
        try {
            return paramDataService.getParamBoListByType(type);
        } catch (DAOException e) {
            throw new BizException(e);
        } finally {
        }
    }

    @Override
    public List<ParamBO> getParamBOList() throws BizException {
        try {
            return paramDataService.getParamBOList();
        } catch (DAOException e) {
            throw new BizException(e);
        } finally {
        }
    }
}