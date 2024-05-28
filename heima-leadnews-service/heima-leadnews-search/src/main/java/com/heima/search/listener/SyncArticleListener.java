package com.heima.search.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.ArticleConstants;
import com.heima.model.search.vos.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SyncArticleListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @KafkaListener(topics = ArticleConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String message) {
        if (StringUtils.isNotEmpty(message)) {
            log.info("收到消息，添加搜索索引");
            SearchArticleVo searchArticleVo = JSON.parseObject(message, SearchArticleVo.class);

            IndexRequest indexRequest = new IndexRequest("app_info_article")
                    .id(searchArticleVo.getId().toString())
                    .source(message, XContentType.JSON);

            try {
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                log.info("添加搜索索引成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
