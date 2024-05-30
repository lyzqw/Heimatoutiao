package com.heima.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamQuickStart {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.0.100:9092");
        //string 序列化期
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"streams-quickstart");
//        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
//        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
//        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");


        StreamsBuilder streamsBuilder = new StreamsBuilder();
        //流式计算
        streamProcessor(streamsBuilder);

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);
        kafkaStreams.start();
    }

    /**
     * 消息的内容：hello kafka  hello itcast
     * @param streamsBuilder
     */
    private static void streamProcessor(StreamsBuilder streamsBuilder) {
        //创建kstream对象，同时指定从那个topic中接收消息
        KStream<String, String> stream = streamsBuilder.stream("itcast-topic-input");//receive message



        //聚合处理
         stream.flatMapValues(new ValueMapper<String, Iterable<String>>() {
            @Override
            public Iterable<String> apply(String value) { //这个value就是消息的内容：hello kafka  hello itcast
                return Arrays.asList(value.split(" "));//因为是计算单词数量，所以分割空格
            }
        }).groupBy(new KeyValueMapper<String, String, Object>() {
            @Override
            public Object apply(String key, String value) {
                return value;
            }
        }).windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                .count()
                .toStream()
                .map((k,v)->{
                    System.out.println("key:"+k+",vlaue:"+v);
                    return new KeyValue<>(k.key().toString(),v.toString());
                })
                 //发送消息
                 .to("itcast-topic-out");
    }
}
