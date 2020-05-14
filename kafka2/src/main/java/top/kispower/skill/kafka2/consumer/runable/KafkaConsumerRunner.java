package top.kispower.skill.kafka2.consumer.runable;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;
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
     * 订阅的topic
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
     * 当前偏移量
     */
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    /**
     * 提交位移
     */
    private CommitPolicy commitPolicy = new CommitPolicy();

    /**
     * 提交结果回调
     */
    private OffsetCommitCallback offsetCommitCallback = new CallbackPolicy();

    /**
     * 再均衡监听器
     */
    private ConsumerRebalanceListener consumerRebalanceListener;

    public KafkaConsumerRunner(Properties properties, List<String> topicList, RecordHandler recordHandler) {
        this.kafkaConsumer = new KafkaConsumer(properties);
        this.topicList = topicList;
        this.recordHandler = recordHandler;
        this.consumerRebalanceListener = new RebalancePolicy(kafkaConsumer);
    }

    /**
     * run方法（TODO: 异常处理）
     *
     * @author haisenbao
     * @date 2020/5/13
     */
    @Override
    public void run() {
        try {
            // 订阅topic
            kafkaConsumer.subscribe(topicList, consumerRebalanceListener);

            while (!closed.get()) {

                // 拉取消息
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10000));

                for (ConsumerRecord<String, String> record : records) {
                    // 业务处理
                    recordHandler.handRecord(record);

                    // 保存偏移量
                    saveCurrentOffset(record);

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
     * 保存偏移量
     */
    private void saveCurrentOffset(ConsumerRecord<String, String> record) {
        currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()));
    }

    /**
     * 停止消费
     */
    public void stopConsumer() {
        this.closed.set(true);
    }

    /**
     * 业务处理（TODO：消息补偿）
     *
     * @author haisenbao
     * @date 2020/5/13
     */
    public interface RecordHandler {

        /**
         * 包装层
         */
        default void handRecord(ConsumerRecord<String, String> record) {
            try {
                process(record);
            } catch (Exception e) {
                log.error("top.kispower.skill.kafka2.consumer.runable.KafkaConsumerRunner.RecordHandler.process failed, record={}", record, e);
            }
        }

        /**
         * 消息的处理逻辑(需业务方实现)
         */
        void process(ConsumerRecord<String, String> record);
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
        private int consumerCount = 0;

        void commit(Consumer consumer, ConsumerRecord<String, String> record) {
            currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()));
            if (++consumerCount % BATCH_SIZE == 0) {
                consumer.commitAsync(currentOffsets, offsetCommitCallback);
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

    /**
     * 消费者再均衡处理策略
     *
     * @author haisenbao
     * @date 2020/5/13
     */
    private class RebalancePolicy implements ConsumerRebalanceListener {

        private Consumer consumer;

        RebalancePolicy(Consumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            log.info("onPartitionsRevoked...currentOffsets={}", currentOffsets);
            this.consumer.commitAsync(currentOffsets, offsetCommitCallback);
            currentOffsets.clear();
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

        }
    }
}
