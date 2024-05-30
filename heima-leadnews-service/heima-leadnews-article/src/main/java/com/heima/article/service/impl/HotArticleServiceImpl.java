package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private IWemediaClient wemediaClient;
    @Autowired
    private CacheService cacheService;

    @Override
    public void computeHotArticle() {
        //查询前5天
        Date dateParam = DateTime.now().minusDays(5).toDate();
        List<ApArticle> articleList = apArticleMapper.findArticleListByLast5days(dateParam);
        //计算文章的分值
        List<HotArticleVo> hotArticleVosList = computeHotArticle(articleList);
        //为每个频道缓存30条分值高的文章
        cacheTagToRedis(hotArticleVosList);
    }

    private void cacheTagToRedis(List<HotArticleVo> hotArticleVosList) {
        ResponseResult responseResult = wemediaClient.getChannels();
        if (responseResult.getCode().equals(200)) {
            String channelJson = JSON.toJSONString(responseResult.getData());
            List<WmChannel> channelList = JSON.parseArray(channelJson, WmChannel.class);
            if (channelList != null && channelList.size() > 0) {
                for (WmChannel channel : channelList) {
                    //找到同一个频道的
                    List<HotArticleVo> list = hotArticleVosList.stream()
                            .filter(hotArticleVo -> {
                                Integer channelId = hotArticleVo.getChannelId();
                                Integer id = channel.getId();
                                System.out.println("hotArticleVo.channelId: "+channelId);
                                System.out.println("channel.channelId: "+channelId);
                                return Objects.equals(channelId,id);
                            }).collect(Collectors.toList());
                    sortAndCache(
                            list, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + channel.getId()
                    );
                }
            }
        }
        //设置推荐数据
        //给文章进行排序，取30条分值较高的文章存入redis  key：默认tag   value：30条分值较高的文章
        sortAndCache(hotArticleVosList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    private void sortAndCache(List<HotArticleVo> list, String key) {
        List<HotArticleVo> articleVos = list.stream().sorted(Comparator.comparing(new Function<HotArticleVo, Integer>() {
            @Override
            public Integer apply(HotArticleVo hotArticleVo) {
                return hotArticleVo.getScore();
            }
        }).reversed()).collect(Collectors.toList());
        if (articleVos.size() > 30) {
            articleVos = articleVos.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(articleVos));
    }

    private List<HotArticleVo> computeHotArticle(List<ApArticle> apArticles) {
        if (apArticles.isEmpty()) return Collections.emptyList();
        List<HotArticleVo> dataList = new ArrayList<>();
        for (ApArticle article : apArticles) {
            HotArticleVo hotArticleVo = new HotArticleVo();
            BeanUtils.copyProperties(article, hotArticleVo);
            hotArticleVo.setScore(computeScore(article));
            dataList.add(hotArticleVo);
        }
        return dataList;
    }

    private Integer computeScore(ApArticle article) {
        int score = 0;
        if (article.getLikes() != null) {
            score += article.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (article.getViews() != null) {
            score += article.getViews();
        }
        if (article.getComment() != null) {
            score += article.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (article.getCollection() != null) {
            score += article.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }
        return score;
    }
}
