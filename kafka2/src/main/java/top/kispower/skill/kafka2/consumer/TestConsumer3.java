package top.kispower.skill.kafka2.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import top.kispower.skill.kafka2.annotation.KafkaConsumer;
import top.kispower.skill.kafka2.consumer.runable.KafkaConsumerRunner;

/**
 * @author haisenbao
 * @date 2020/5/13
 */
@Slf4j
@Component
@KafkaConsumer(groupId = "group3", topicList = {"test3"}, consumerThreadNum = 2)
public class TestConsumer3 implements KafkaConsumerRunner.RecordHandler{

    @Override
    public boolean process(ConsumerRecord<String, String> record) {
        log.info("{}", record);
        return true;
    }


}
