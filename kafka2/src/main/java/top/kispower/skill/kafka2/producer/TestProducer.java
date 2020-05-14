package top.kispower.skill.kafka2.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author haisenbao
 * @date 2020/5/14
 */
@Slf4j
@Component
public class TestProducer {

    private static final String TOPIC = "test3";

    @Resource
    private BaseProducer baseProducer;

    public void sendMessage(String key, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, key, message);
        try {
            RecordMetadata recordMetadata = baseProducer.sendAndGet(record);
            log.info("TestProducer.sendMessage success, record={}, recordMetadata={}", record, recordMetadata);
        } catch (Exception e) {
            log.error("TestProducer.sendMessage failed, record={}", record, e);
        }
    }

}
