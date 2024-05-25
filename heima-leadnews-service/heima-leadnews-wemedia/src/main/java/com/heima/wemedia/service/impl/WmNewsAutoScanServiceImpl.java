package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;


@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Override
    @Async
    public void autoScanWmNews(Integer id) {
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }
        if (!Objects.equals(wmNews.getStatus(), WmNews.Status.SUBMIT.getCode())) {
            return;
        }

        //从内容中提取纯文本内容和图片
        Map<String, Object> textAndImages = handleTextAndImages(wmNews);
        //2.审核文本内容  阿里云接口
        boolean isTextScan = handleTextScan((String) textAndImages.get("content"),wmNews);
        if (!isTextScan){
            return;
        }
        //3.审核图片  阿里云接口

        //4.审核成功，保存app端的相关的文章数据
        ResponseResult responseResult = saveAppArticle(wmNews);
        if (responseResult.getCode() != 200) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
        }
        wmNews.setArticleId((Long) responseResult.getData());
        updateWmNews(wmNews, (short) 9, "审核成功");
    }

    private boolean handleTextScan(String content, WmNews wmNews) {
        if (content.contains("毒")){
            updateWmNews(wmNews, (short) 2,"当前文章中存在违规内容");
            return false;
        }

        return true;
    }

    /**
     * //存储纯文本内容
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringUtils.isEmpty(wmNews.getContent())) {
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }
            }
        }
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("content", stringBuilder.toString());
        return resultMap;
    }

    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto dto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, dto);
        //文章的布局
        dto.setLayout(wmNews.getType());
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            //频道
            dto.setChannelName(wmChannel.getName());
        }
        //作者
        dto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            dto.setAuthorName(wmUser.getName());
        }
        //设置文章id
        if (wmNews.getArticleId() != null) {
            dto.setId(wmNews.getArticleId());
        }
        dto.setCreatedTime(new Date());

        return articleClient.saveArticle(dto);
    }

    private void updateWmNews(WmNews wmNews, short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }
}
