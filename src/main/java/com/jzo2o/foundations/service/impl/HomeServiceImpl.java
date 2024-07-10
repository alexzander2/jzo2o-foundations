package com.jzo2o.foundations.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.jzo2o.foundations.service.IServeService;
import com.jzo2o.redis.annotations.HashCacheClear;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class HomeServiceImpl implements HomeService {
    @Resource
    private ServeMapper serveMapper;
    @Resource
    private IRegionService iRegionService;


    @Override
    @Caching(cacheable = {
            @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() != 0",cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),//使用unless的时候需要取反
            @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() == 0",cacheManager = RedisConstants.CacheManager.FOREVER)
    })
    public List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId) {
        Region byId = iRegionService.getById(regionId);
        if(ObjectUtils.isNull(byId) || byId.getActiveStatus() != FoundationStatusEnum.ENABLE.getStatus()){
            return Collections.emptyList();
        }
        List<ServeCategoryResDTO> serveIconCategoryByRegionId = serveMapper.findServeIconCategoryByRegionId(regionId);
        if(CollUtils.isEmpty(serveIconCategoryByRegionId)){
            return Collections.emptyList();
        }
        int endIndex = serveIconCategoryByRegionId.size() > 2 ? 2 : 1; //首页仅展示两行
        List<ServeCategoryResDTO> serveCategoryResDTOS = new ArrayList<>(serveIconCategoryByRegionId.subList(0, endIndex));
        serveCategoryResDTOS.forEach(item -> {
            int end = item.getServeResDTOList().size() > 4 ? 4 : item.getServeResDTOList().size();
            ArrayList<ServeSimpleResDTO> serveSimpleResDTOS = new ArrayList<>(item.getServeResDTOList().subList(0, end));
            item.setServeResDTOList(serveSimpleResDTOS);
        });
        return serveCategoryResDTOS;
    }
}
