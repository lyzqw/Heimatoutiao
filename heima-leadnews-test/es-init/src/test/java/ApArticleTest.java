import com.alibaba.fastjson.JSON;
import com.heima.es.EsInitApplication;
import com.heima.es.mapper.ApArticleMapper;
import com.heima.es.pojo.SearchArticleVo;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = EsInitApplication.class)
@RunWith(SpringRunner.class)
public class ApArticleTest {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void init() throws Exception {
        List<SearchArticleVo> articleList = apArticleMapper.loadArticleList();
        //2.批量导入到es索引库
        BulkRequest bulkRequest = new BulkRequest("app_info_article");

        for (SearchArticleVo articleVo : articleList) {
            IndexRequest request = new IndexRequest().id(articleVo.getId().toString()).source(JSON.toJSON(articleVo), XContentType.JSON);
            bulkRequest.add(request);
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }


}
