package top.kispower.skill.kafka2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.kispower.skill.kafka2.producer.TestProducer;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class KafkaTest {

    @Resource
    private TestProducer testProducer;

    @Test
    void sendRecord() {
        testProducer.sendMessage("messageKey", "testMessage123");
    }

    @Test
    void consumerRecord() throws InterruptedException {
        TimeUnit.HOURS.sleep(1);
    }
}
