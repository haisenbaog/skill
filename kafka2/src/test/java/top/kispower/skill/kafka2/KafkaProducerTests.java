package top.kispower.skill.kafka2;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.kispower.skill.kafka2.producer.BaseProducer;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

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
        ProducerRecord<String, String> record = new ProducerRecord<>("test3", "sendRecordTest...");
        baseProducer.sendWithPolicy(record, BaseProducer.LogCallbackPolicy.class);
    }

    @Test
    void consumerRecord() throws InterruptedException {
        TimeUnit.HOURS.sleep(1);
    }
}
