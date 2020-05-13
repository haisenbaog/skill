package top.kispower.skill.kafka2.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaConsumer {

    String groupId();

    String[] topicList();

    int consumerThreadNum() default 10;
}
