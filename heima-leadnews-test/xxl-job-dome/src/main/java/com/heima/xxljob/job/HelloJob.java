package com.heima.xxljob.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class HelloJob {

    @Value("${server.port}")
    private String port;


    //轮询执行比如在    2024-05-29 08:51:27 这个时间点只有一台服务器在处理任务
    //分片可以实现并发进行处理，把一个大任务拆分成2个或多个，每个机器对应处理自己哪个片的任务。这个2024-05-29 08:51:27时间点可以实现多台机器都在处理任务
    @XxlJob("demoJobHandler")
    public void helloJob() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        System.out.println("简单任务执行了。。。。" + port +"时间："+ format);
    }

}
