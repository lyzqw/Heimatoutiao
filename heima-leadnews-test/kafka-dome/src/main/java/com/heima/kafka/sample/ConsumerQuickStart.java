package com.heima.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerQuickStart {

    /**
     * //1.添加kafka的配置信息
     * Properties properties = new Properties();
     * //kafka的连接地址
     * properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.130:9092");
     * //消费者组
     * properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
     * //消息的反序列化器
     * properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
     * properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
     * <p>
     * //2.消费者对象
     * KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
     * <p>
     * //3.订阅主题
     * consumer.subscribe(Collections.singletonList("itheima-topic"));
     * <p>
     * //当前线程一直处于监听状态
     * while (true) {
     * //4.获取消息
     * ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
     * for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
     * System.out.println(consumerRecord.key());
     * System.out.println(consumerRecord.value());
     * }
     * }
     *
     * @param args
     */
//    public static void main(String[] args) {
//        Properties properties = new Properties();
//        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//
//        //消费者对象
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
//
//        //订阅主题
//        consumer.subscribe(Collections.singletonList("itheima-topic"));
//
//        while (true) {
//            System.out.println("start poll");
//            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
//            System.out.println("poll:" + consumerRecords.count());
//            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
//                System.out.println("key: " + consumerRecord.key());
//                System.out.println("value: " + consumerRecord.value());
//
//            }
//        }
//
//    }
    public static void main(String[] args) {
        //1.添加kafka的配置信息
        Properties properties = new Properties();
        //kafka的连接地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.101:9092");
        //消费者组
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
        //消息的反序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        //2.消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        //3.订阅主题
        consumer.subscribe(Collections.singletonList("itheima-topic"));

        //当前线程一直处于监听状态
        while (true) {
            //4.获取消息
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                System.out.println(consumerRecord.key());
                System.out.println(consumerRecord.value());
            }
        }

    }
}
