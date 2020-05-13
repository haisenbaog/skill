package top.kispower.skill.kafka2.consumer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author haisenbao
 * @date 2020/5/13
 */
@Slf4j
public class KafkaConsumerRunner implements Runnable {

    /**
     * kafka消费者
     */
    private final KafkaConsumer kafkaConsumer;

    /**
     * 消费的topic
     */
    private final List<String> topicList;

    /**
     * 消费开关
     */
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * 处理业务
     */
    private final RecordHandler recordHandler;

    /**
     * 提交位移
     */
    private CommitPolicy commitPolicy = new CommitPolicy();

    /**
     * 提交结果回调
     */
    private OffsetCommitCallback offsetCommitCallback = new CallbackPolicy();

    public KafkaConsumerRunner(Properties properties, List<String> topicList, RecordHandler recordHandler) {
        this.kafkaConsumer = new KafkaConsumer(properties);
        this.topicList = topicList;
        this.recordHandler = recordHandler;
    }

    @Override
    public void run() {
        try {
            // 订阅topic
            kafkaConsumer.subscribe(topicList);
            while (!closed.get()) {
                // 拉取消息
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10000));
                for (ConsumerRecord<String, String> record : records) {
                    // 业务处理
                    recordHandler.hand(record);
                    // 提交偏移量
                    commitPolicy.commit(kafkaConsumer, record);
                }
            }
        } catch (WakeupException e) {
            if (!closed.get()) {
                throw e;
            }
        } finally {
            kafkaConsumer.close();
        }
    }

    /**
     * 停止消费
     */
    public void stopConsumer() {
        this.closed.set(true);
    }

    /**
     * 业务处理
     *
     * @author haisenbao
     * @date 2020/5/13
     */
    public interface RecordHandler {
        // 补偿
        void hand(ConsumerRecord<String, String> record);
    }

    /**
     * 位移提交
     *
     * @author haisenbao
     * @date 2020/5/13
     */
    @Data
    private class CommitPolicy {
        private static final int BATCH_SIZE = 5;
        private Map<TopicPartition, OffsetAndMetadata> offsetMap = new HashMap<>();
        private int consumerCount = 0;

        void commit(Consumer consumer, ConsumerRecord<String, String> record) {
            offsetMap.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()));
            if (++consumerCount % BATCH_SIZE == 0) {
                consumer.commitAsync(offsetMap, offsetCommitCallback);
            }
        }

    }

    /**
     * 回调策略
     *
     * @author haisenbao
     * @date 2020/5/13
     */
    @Data
    private class CallbackPolicy implements OffsetCommitCallback {
        @Override
        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
            if (exception == null) {
                log.info("commitAsync success: offsets={}", offsets);
            } else {
                log.error("commitAsync failed: offsets={}", offsets, exception);
            }
        }
    }
}
