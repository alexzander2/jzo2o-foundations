package com.jzo2o.foundations.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

//区域服务管理相关接口（运维人员使用）
//如果不写Bean的名称，在Bean管理的时候，默认名称为首字母小写，会存在注入问题（如不同包有相同名称的Controller）
@RestController("operationServeController")
@RequestMapping("/operation/serve")
@Api(tags = "运营端 区域服务管理相关接口")
public class ServeController {

    @Resource
    private IServeService serveService;



    @GetMapping("/page")
    @ApiOperation("区域服务分页查询")
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO){
//        return null;
        return serveService.page(servePageQueryReqDTO);
    }

    @PostMapping("/batch")
    @ApiOperation("添加区域服务") //@Requestbody 得到json数据 上面的page方法因为是从form表单中取数据所以不需要
    public void add(@RequestBody List<ServeUpsertReqDTO> serveUpsertReqDTOList){
        serveService.batchAdd(serveUpsertReqDTOList);
    }

    @PutMapping("/{id}")
    @ApiOperation("区域服务修改价格")
    public void update(@PathVariable("id") Long id, BigDecimal price){
        Serve update = serveService.update(id, price);

    }

    @PutMapping("/onSale/{id}")
    @ApiOperation(("区域服务上架"))
    public void onSale(@PathVariable("id") Long id){
        Serve serve = serveService.onSale(id);
    }
}
