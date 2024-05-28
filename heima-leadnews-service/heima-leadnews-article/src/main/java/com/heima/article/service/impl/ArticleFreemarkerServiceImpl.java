package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.search.vos.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private Configuration configuration;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Async
    @Override
    public void buildArticleToMinIO(ApArticle apArticle, String content) {
        if (StringUtils.isNotBlank(content)) {
            //2.文章内容通过freemarker生成html文件
            StringWriter out = new StringWriter();
            Template template = null;
            try {
                template = configuration.getTemplate("article.ftl");
                Map<String, Object> params = new HashMap<>();
                params.put("content", JSONArray.parseArray(content));

                template.process(params, out);
                InputStream is = new ByteArrayInputStream(out.toString().getBytes());
                //3.把html文件上传到minio中
                String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", is);
                System.out.println("path: " + path);
                //4.修改ap_article表，保存static_url字段
                ApArticle article = new ApArticle();
                article.setId(apArticle.getId());
                article.setStaticUrl(path);
                apArticleMapper.updateById(article);
                //发送消息，创建索引
                createArticleESIndex(apArticle, content, path);
            } catch (Exception e) {
                log.info("上传minio 失败，");
                createArticleESIndex(apArticle, content, "");
            }

        }
    }

    private void createArticleESIndex(ApArticle apArticle, String content, String path) {
        SearchArticleVo vo = new SearchArticleVo();
        BeanUtils.copyProperties(apArticle, vo);
        vo.setStaticUrl(path);
        vo.setContent(content);
        log.info("发送文章 创建索引");
        kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(vo));
    }
}
