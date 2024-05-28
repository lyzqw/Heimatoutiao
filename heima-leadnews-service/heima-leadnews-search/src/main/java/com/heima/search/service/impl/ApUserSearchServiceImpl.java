package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.HistorySearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.ApUserThreadLocalUtil;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service
public class ApUserSearchServiceImpl implements ApUserSearchService {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Async
    public void insert(String keyword, Integer userId) {
        //1.查询当前用户的搜索关键词
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword));
        ApUserSearch userSearch = mongoTemplate.findOne(query, ApUserSearch.class);
        //2.存在 更新创建时间
        if (userSearch != null) {
            userSearch.setCreatedTime(new Date());
            mongoTemplate.save(userSearch);
            return;
        }
        //3.不存在，判断当前历史记录总数量是否超过10
        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keyword);
        apUserSearch.setCreatedTime(new Date());

        Query query1 = Query.query(Criteria.where("userId").is(userId));
        query1.with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<ApUserSearch> apUserSearchList = mongoTemplate.find(query1, ApUserSearch.class);
        if (apUserSearchList != null && apUserSearchList.size() >= 10) {
            ApUserSearch lastUserSearch = apUserSearchList.get(apUserSearchList.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearch.getId())), apUserSearch);
        } else {
            mongoTemplate.save(apUserSearch);
        }
    }

    @Override
    public ResponseResult findUserSearch() {
        ApUser user = ApUserThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Query query = Query.query(Criteria.where("userId").is(user.getId())).with(Sort.by(Sort.Direction.DESC, "createdTime"));

        List<ApUserSearch> searchList = mongoTemplate.find(query, ApUserSearch.class);
        return ResponseResult.okResult(searchList);
    }

    @Override
    public ResponseResult delUserSearch(HistorySearchDto historySearchDto) {
        //1.检查参数
        if(historySearchDto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.判断是否登录
        ApUser user = ApUserThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        DeleteResult deleteResult = mongoTemplate.remove(Query.query(Criteria.where("id").is(historySearchDto.getId())), ApUserSearch.class);
        boolean acknowledged = deleteResult.wasAcknowledged();
        log.info("delUserSearch: "+acknowledged);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
