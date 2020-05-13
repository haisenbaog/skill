package top.kispower.skill.kafka2.demo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author haisenbao
 * @date 2020/5/11
 */
@Slf4j
public class DemoConsumer {

    private static List<String> topicList = Arrays.asList("test3");
    private static boolean seekedToBegin = false;
    private static final int BATCH_SIZE = 5;
    private static int consumerCount = 0;
    private static Map<TopicPartition, OffsetAndMetadata> offsetMap = new HashMap<>();
    private static OffsetCommitCallback commitCallback = (committedOffsetMap, exception) -> {
        if (exception == null) {
            log.info("commitAsync success: committedOffsetMap={}", committedOffsetMap);
            System.err.println("commitAsync success: committedOffsetMap=" + committedOffsetMap);
        } else {
            log.error("commitAsync failed", exception);
            System.err.println("commitAsync failed " + exception);
        }
    };

    private static Consumer getConsumer() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "101.132.110.204:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return new KafkaConsumer<>(props);
    }

    private static void processRecord(ConsumerRecord<String, String> record) {
        log.info("processRecord... record={}", record);
        System.err.println("processRecord... record=" + record);
    }

    private static void commit(Consumer consumer, ConsumerRecord<String, String> record) {
        offsetMap.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()));
        if (++consumerCount % BATCH_SIZE == 0) {
            consumer.commitAsync(offsetMap, commitCallback);
        }
    }

    private static class ConsumerRebalanceListenerImpl implements ConsumerRebalanceListener {

        private Consumer consumer;

        ConsumerRebalanceListenerImpl(Consumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> collection) {
            log.info("onPartitionsRevoked...offsetMap={}", offsetMap);
            System.err.println("onPartitionsRevoked...offsetMap=" + offsetMap);
            this.consumer.commitAsync(offsetMap, commitCallback);
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> collection) {

        }
    }

    public static void main(String[] args) {
        try (Consumer consumer = getConsumer()) {
            consumer.subscribe(topicList, new ConsumerRebalanceListenerImpl(consumer));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(2000));

                // 从第一个消息开始消费
                if (!seekedToBegin) {
                    consumer.seekToBeginning(records.partitions());
                    seekedToBegin = true;
                    continue;
                }

                for (ConsumerRecord<String, String> record : records) {
                    processRecord(record);
                    commit(consumer, record);
                }

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    log.error("InterruptedException", e);
                }

//                if (records.isEmpty()) {
//                    log.warn("消费完毕...");
//                    System.err.println("消费完毕...");
//                    break;
//                }
            }
        }
    }


}
