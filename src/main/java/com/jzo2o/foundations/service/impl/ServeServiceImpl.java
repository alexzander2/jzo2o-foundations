package com.jzo2o.foundations.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务表 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-07-02
 */
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {
    @Resource
    private ServeItemMapper serveItemMapper;

    @Resource
    private RegionMapper regionMapper;

    @Override
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
        return PageHelperUtils.selectPage(servePageQueryReqDTO,()->
                baseMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));
    }

    @Override
    public void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList) {
    //合法性校验
        //serve item是否启用，如果未启用则不能添加
        //同一个区域下不能添加相同的服务
        //向serve表中插入数据
        for(ServeUpsertReqDTO serveUpsertReqDTO : serveUpsertReqDTOList){
            ServeItem serveItem = serveItemMapper.selectById(serveUpsertReqDTO.getServeItemId());
            if(ObjectUtils.isNull(serveItem) || serveItem.getActiveStatus() != FoundationStatusEnum.ENABLE.getStatus()){
                //抛出异常
                throw new ForbiddenOperationException("服务项不存在或服务项未启用，不允许添加");
            }
            Integer count = lambdaQuery().eq(Serve::getServeItemId, serveUpsertReqDTO.getServeItemId())
                    .eq(Serve::getRegionId, serveUpsertReqDTO.getRegionId()).count();
            if(count > 0){
                throw new ForbiddenOperationException(serveItem.getName() + "服务已存在");
            }
            Serve serve = BeanUtils.toBean(serveUpsertReqDTO, Serve.class);
            serve.setCityCode(regionMapper.selectById(serve.getRegionId()).getCityCode());
            baseMapper.insert(serve);
        }
    }

    @Override
    public Serve update(Long id, BigDecimal price) {
        boolean update = lambdaUpdate().eq(Serve::getId, id)
                .set(Serve::getPrice, price)
                .update();
        if(!update){
            throw new CommonException("修改服务价格失败");
        }
        Serve serve = baseMapper.selectById(id);
        return serve;
    }

    @Override
    public Serve onSale(Long id) {
        //根据id查询serve信息
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtils.isNull(serve)){
            throw new ForbiddenOperationException("区域服务信息不存在");
        }
        //如果serve的sale status是0或者1表示可以上架
        if(serve.getSaleStatus() == FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("服务已经上架了~");
        }
        if(serveItemMapper.selectById(serve.getServeItemId()).getActiveStatus() != FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("服务项没有启用，不能上架");
        }
        boolean update = lambdaUpdate().eq(Serve::getId, id).set(Serve::getSaleStatus, 2).update();
        if(!update){
            throw  new ForbiddenOperationException("服务上架失败");
        }
        return baseMapper.selectById(id);
    }
}
