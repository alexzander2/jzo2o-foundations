package com.jzo2o.foundations.service;

import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;

import java.util.List;

public interface ServeAggregationService {
    List<ServeSimpleResDTO> findServeList(String cityCode, Long serveTypeId, String keyword);
}

