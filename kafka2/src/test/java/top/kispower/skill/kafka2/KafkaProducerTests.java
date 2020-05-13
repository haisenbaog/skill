package top.kispower.skill.kafka2;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.kispower.skill.kafka2.demo.producer.BaseProducer;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

@SpringBootTest
class KafkaProducerTests {

    @Resource
    private BaseProducer baseProducer;

    @Test
    void sendRecord1() {
        ProducerRecord<String, String> record = new ProducerRecord<>("test", "sendRecordTest...");
        baseProducer.send(record);
    }

    @Test
    void sendRecord2() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ProducerRecord<String, String> record = new ProducerRecord<>("test", "sendRecordTest...");
        baseProducer.sendWithPolicy(record, BaseProducer.LogCallbackPolicy.class);
    }

}
