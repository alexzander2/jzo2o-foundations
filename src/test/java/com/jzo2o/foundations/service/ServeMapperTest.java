package com.jzo2o.foundations.service;

import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
public class ServeMapperTest {
    @Resource
    private ServeMapper serveMapper;

    @Test
    public void test_queryServeListByRegionId(){
        List<ServeResDTO> serveResDTOS = serveMapper.queryServeListByRegionId(1677152267410149378L);
        System.out.println(Arrays.toString(serveResDTOS.toArray()));
    }
}
