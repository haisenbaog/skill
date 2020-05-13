package top.kispower.skill.kafka2.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.stereotype.Component;
import top.kispower.skill.kafka2.config.KafkaConsumerConfig;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author haisenbao
 * @date 2020/5/13
 */
@Slf4j
@Component
public class KafkaConsumerRegister {

    @Resource
    private KafkaConsumerConfig kafkaConsumerConfig;

    /**
     * 线程池 TODO：手动创建(数量，名称)
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    ThreadFactory threadFactory = new ThreadFactory() {
        private String groupId;
        private List<String> topicList;
        int consumerThreadNum;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread();
            thread.setName(String.format("KafkaConsumerThread-%s-%s-%d", groupId, topicList.toString(), consumerThreadNum));

            return null;
        }
    };

    /**
     * 注册消费者
     *
     * @author haisenbao
     * @date 2020/5/13
     */
    public void registerConsumer(String groupId, List<String> topicList, int consumerThreadNum, KafkaConsumerRunner.RecordHandler recordHandler) {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerConfig.getBootstrapServers());
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfig.getKeyDeserializer());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConsumerConfig.getValueDeserializer());
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConsumerConfig.getEnableAutoCommit());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        for (int i = 0; i < consumerThreadNum; i++) {
            executorService.submit(new KafkaConsumerRunner(props, topicList, recordHandler));
        }
    }
}
