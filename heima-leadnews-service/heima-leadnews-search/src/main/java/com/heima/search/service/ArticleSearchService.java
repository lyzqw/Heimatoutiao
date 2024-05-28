package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.UserSearchDto;

import java.io.IOException;

public interface ArticleSearchService {

    ResponseResult search(UserSearchDto userSearchDto) throws IOException;

}
