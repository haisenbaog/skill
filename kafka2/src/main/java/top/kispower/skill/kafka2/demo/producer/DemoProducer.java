package top.kispower.skill.kafka2.demo.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author haisenbao
 * @date 2020/5/11
 */
@Slf4j
public class DemoProducer {

    private static Producer<String, String> getProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "101.132.110.204:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<>(props);
    }

    private static void send(Producer<String, String> producer, ProducerRecord<String, String> record) {
        producer.send(record);
    }

    private static RecordMetadata sendAndGetRecordMetadata(Producer<String, String> producer, ProducerRecord<String, String> record) throws ExecutionException, InterruptedException {
        return producer.send(record).get();
    }

    private static void sendWithCallBack(Producer<String, String> producer, ProducerRecord<String, String> record, Callback callback) {
        producer.send(record, callback);
    }

    public static void main(String[] args) {
        try (Producer<String, String> producer = getProducer()) {
            ProducerRecord<String, String> record = new ProducerRecord<>("test3", "testMsg3");
            Callback callback = (recordMetadata, exception) -> {
                if (exception == null) {
                    log.info("send success: record={}, recordMetadata={}", record, recordMetadata);
                }else {
                    log.error("send failed: record={}", record, exception);
                }
            };
            sendWithCallBack(producer, record, callback);
        }
    }
}
