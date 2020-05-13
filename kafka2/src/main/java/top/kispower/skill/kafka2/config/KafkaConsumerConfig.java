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



    private String groupId;
}
