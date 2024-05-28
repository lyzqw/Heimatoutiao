package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.HistorySearchDto;

public interface ApUserSearchService {

    public void insert(String keyword, Integer userId);

    ResponseResult findUserSearch();

    ResponseResult delUserSearch(HistorySearchDto historySearchDto);
}
