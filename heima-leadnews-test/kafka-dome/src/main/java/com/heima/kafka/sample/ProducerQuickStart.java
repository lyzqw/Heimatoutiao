package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class ProducerQuickStart {


    /**
     * //1.kafka的配置信息
     * Properties properties = new Properties();
     * //kafka的连接地址
     * properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.130:9092");
     * //发送失败，失败的重试次数
     * properties.put(ProducerConfig.RETRIES_CONFIG,5);
     * //消息key的序列化器
     * properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
     * //消息value的序列化器
     * properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
     * <p>
     * //2.生产者对象
     * KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);
     * <p>
     * //封装发送的消息
     * ProducerRecord<String,String> record = new ProducerRecord<String, String>("itheima-topic","100001","hello kafka");
     * <p>
     * //3.发送消息
     * producer.send(record);
     * <p>
     * //4.关闭消息通道，必须关闭，否则消息发送不成功
     * produc er.close();
     *
     * @param args
     */
//    public static void main(String[] args) {
//        //1.kafka的配置信息
//        Properties properties = new Properties();
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//        properties.put(ProducerConfig.RETRIES_CONFIG, 5);
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//
//        //2.生产者对象
//        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
//        //封装发送的消息
//        ProducerRecord<String, String> record = new ProducerRecord<String, String>("itheima-topic", "100001", "hello kafka");
//        //3.发送消息
////        producer.send(record, new Callback() {
////            @Override
////            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
////                System.out.println("recordMetadata: "+recordMetadata);
////                System.out.println("e: "+e.getMessage());
////            }
////        });
//        System.out.println("send");
//        producer.send(record);
//        //4.关闭消息通道，必须关闭，否则消息发送不成功
//        producer.close();
//        System.out.println("close");
//
//
//    }
    public static void main(String[] args) {
        //1.kafka的配置信息
        Properties properties = new Properties();
        //kafka的连接地址
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.100:9092");
        //发送失败，失败的重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG, 5);
        //消息key的序列化器
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //消息value的序列化器
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        //2.生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //封装发送的消息
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("itheima-topic", "100001", "hello kafka22");

        //3.发送消息
        producer.send(record);

        //4.关闭消息通道，必须关闭，否则消息发送不成功
        producer.close();
    }

}
