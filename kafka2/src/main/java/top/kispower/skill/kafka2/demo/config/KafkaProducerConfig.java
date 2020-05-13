package top.kispower.skill.kafka2.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haisenbao
 * @date 2020/5/12
 */
@Data
@Component
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerConfig {

    private String bootstrapServers;

    private String keySerializer;

    private String valueSerializer;

    private String acks;
}

