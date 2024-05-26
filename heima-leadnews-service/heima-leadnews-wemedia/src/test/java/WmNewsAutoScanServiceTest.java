import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.user.utils.common.SensitiveWordUtil;
import com.heima.wemedia.WemediaApplication;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class WmNewsAutoScanServiceTest {

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Test
    public void autoScanWmNews() {
        wmNewsAutoScanService.autoScanWmNews(6234);
    }


    @Test
    public void sensitiveWord() {
        String content = "广1告代理";
        List<WmSensitive> list = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
        SensitiveWordUtil.initMap(list.stream().map(WmSensitive::getSensitives).collect(Collectors.toList()));
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
        if (!map.isEmpty()) {
            System.out.println("包含敏感词: " + map);
        } else {
            System.out.println("不包含");
        }

    }

}
