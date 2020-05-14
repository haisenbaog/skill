package top.kispower.skill.kafka2.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
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
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerConfig.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.getValueSerializer());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfig.getAcks());
        log.info("KafkaProducer building... properties={}", props);
        return new KafkaProducer<>(props);
    }
}
