package top.kispower.skill.kafka2.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import top.kispower.skill.kafka2.annotation.KafkaConsumer;
import top.kispower.skill.kafka2.consumer.KafkaConsumerRegister;
import top.kispower.skill.kafka2.consumer.KafkaConsumerRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author haisenbao
 * @date 2020/5/13
 */
@Slf4j
@Component
public class KafkaConsumerRegisterListener implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {

    private ApplicationContext applicationContext;

    @Resource
    private KafkaConsumerRegister kafkaConsumerRegister;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        registerKafkaConsumer();
    }

    private void registerKafkaConsumer() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(KafkaConsumer.class);
        for (String beanName : beanNames) {
            Object obj = applicationContext.getBean(beanName);
            if (obj instanceof KafkaConsumerRunner.RecordHandler) {
                KafkaConsumerRunner.RecordHandler recordHandler = (KafkaConsumerRunner.RecordHandler) obj;

                KafkaConsumer kafkaConsumer = recordHandler.getClass().getAnnotation(KafkaConsumer.class);
                kafkaConsumerRegister.registerConsumer(kafkaConsumer.groupId(), Arrays.asList(kafkaConsumer.topicList()), kafkaConsumer.consumerThreadNum(), recordHandler);
            }
        }
    }
}
