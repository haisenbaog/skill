package top.kispower.skill.kafka2.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import top.kispower.skill.kafka2.annotation.KafkaConsumer;
import top.kispower.skill.kafka2.config.KafkaConsumerConfig;
import top.kispower.skill.kafka2.consumer.runable.KafkaConsumerRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 根据注解：@KafkaConsumer 注册消费者
 * @author haisenbao
 * @date 2020/5/13
 */
@Slf4j
@Component
public class KafkaConsumerRegisterListener implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {

    private ApplicationContext applicationContext;

    @Resource
    private KafkaConsumerConfig kafkaConsumerConfig;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        registerKafkaConsumer();
    }

    /**
     * 注册消费者
     *
     * @author haisenbao
     * @date 2020/5/14
     */
    private void registerKafkaConsumer() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(KafkaConsumer.class);
        for (String beanName : beanNames) {
            Object obj = applicationContext.getBean(beanName);
            if (obj instanceof KafkaConsumerRunner.RecordHandler) {
                KafkaConsumerRunner.RecordHandler recordHandler = (KafkaConsumerRunner.RecordHandler) obj;

                KafkaConsumer kafkaConsumer = recordHandler.getClass().getAnnotation(KafkaConsumer.class);
                createKafkaConsumerThread(kafkaConsumer.groupId(), Arrays.asList(kafkaConsumer.topicList()), kafkaConsumer.consumerThreadNum(), recordHandler);
            }
        }
    }

    /**
     * 创建消费者及线程
     *
     * @author haisenbao
     * @date 2020/5/13
     */
    private void createKafkaConsumerThread(String groupId, List<String> topicList, int consumerThreadNum, KafkaConsumerRunner.RecordHandler recordHandler) {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerConfig.getBootstrapServers());
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfig.getKeyDeserializer());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfig.getValueDeserializer());
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConsumerConfig.getEnableAutoCommit());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        for (int i = 0; i < consumerThreadNum; i++) {
            KafkaConsumerRunner consumerRunner = new KafkaConsumerRunner(props, topicList, recordHandler);
            String threadName = String.format("KafkaConsumerThread-%s-%s-%d", groupId, topicList.toString(), i);
            new Thread(consumerRunner, threadName).start();
        }
    }
}
