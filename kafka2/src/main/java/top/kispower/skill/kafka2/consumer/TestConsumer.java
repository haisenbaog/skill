package top.kispower.skill.kafka2.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author haisenbao
 * @date 2020/5/13
 */
@Slf4j
@Component
public class TestConsumer {

    @Resource
    private KafkaConsumerRegister kafkaConsumerRegister;

    public void registerConsumer() {
        kafkaConsumerRegister.registerConsumer("testGroup", Collections.singletonList("test3"), 2, record -> {
            log.info("TestConsumer received message: record={}", record);
        });
    }


}
