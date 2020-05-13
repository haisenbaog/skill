package top.kispower.skill.kafka2.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * @author haisenbao
 * @date 2020/5/12
 */
@Slf4j
@Configuration
public class KafkaProducerConfiguration {

    @Resource
    private KafkaProducerConfig kafkaProducerConfig;

    @Bean
    public Producer<String, String> getProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaProducerConfig.getBootstrapServers());
        props.put("acks", kafkaProducerConfig.getAcks());
        props.put("key.serializer", kafkaProducerConfig.getKeySerializer());
        props.put("value.serializer", kafkaProducerConfig.getValueSerializer());
        log.info("KafkaProducer building... properties={}", props);
        return new KafkaProducer<>(props);
    }
}
