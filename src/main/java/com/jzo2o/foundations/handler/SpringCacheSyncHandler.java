package com.jzo2o.foundations.handler;

import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

//import static sun.awt.SunGraphicsCallback.log;
//定期删除
@Component
@Slf4j
public class SpringCacheSyncHandler {
    @Resource
    private RedisTemplate redisTemplate;


    @Resource
    private IRegionService iRegionService;

    @Resource
    private HomeService homeService;
    //定时更新缓存
    @XxlJob("activeRegionCacheSync")//指定任务名称
    public void activeRegionCacheSync(){
    //删除原来的缓存
        String key = RedisConstants.CacheName.JZ_CACHE + "::ACTIVE_REGIONS";
        redisTemplate.delete(key);
        //添加新缓存
        List<RegionSimpleResDTO> regionSimpleResDTOS = iRegionService.queryActiveRegionListCache();
        //遍历区域，对每个区域首页的服务列表进行删除缓存再添加缓存
        regionSimpleResDTOS.forEach(item ->{
            String homepageKey = RedisConstants.CacheName.SERVE_ICON+"::" + item.getId();
            redisTemplate.delete(homepageKey);
            homeService.queryServeIconCategoryByRegionIdCache(item.getId());
        });
    }
}
