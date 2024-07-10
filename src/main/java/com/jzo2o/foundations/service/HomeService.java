package com.jzo2o.foundations.service;

import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;

import java.util.List;

//门户查询接口
public interface HomeService {
    List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId);
}
