package top.kispower.skill.kafka2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haisenbao
 * @date 2020/5/12
 */
@Data
@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerConfig {

    private String bootstrapServers;

    private String keyDeserializer;

    private String valueDeserializer;

    private String enableAutoCommit;

    /**
     * 两次poll之间的最大允许间隔
     * 请不要改得太大，服务器会掐掉空闲连接，不要超过30000
     */
    private Integer sessionTimeoutMs;

    /**
     * 每次poll的最大数量
     * 注意该值不要改得太大，如果poll太多数据，而不能在下次poll之前消费完，则会触发一次负载均衡，产生卡顿
     */
    private Integer maxPollRecords;
}
