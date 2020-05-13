package top.kispower.skill.kafka2.producer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

/**
 * @author haisenbao
 * @date 2020/5/12
 */
@Slf4j
@Component
public class BaseProducer {

    @Resource
    private Producer<String, String> producer;

    /**
     * 发送消息--不关心发送结果
     * @author haisenbao
     * @date  2020/5/13
     */
    public void send(ProducerRecord<String, String> record){
        producer.send(record);
    }

    /**
     * 发送消息--同步获取发送结果（不抛异常即成功）
     * @author haisenbao
     * @date  2020/5/13
     */
    public RecordMetadata sendAndGet(ProducerRecord<String, String> record) throws ExecutionException, InterruptedException {
        return producer.send(record).get();
    }

    /**
     * 发送消息--异步处理发送结果
     * @author haisenbao
     * @date  2020/5/13
     */
    public void send(ProducerRecord<String, String> record, Callback callback){
        producer.send(record, callback);
    }

    /**
     * 发送消息--发送结果处理策略
     * @author haisenbao
     * @date  2020/5/13
     */
    public void sendWithPolicy(ProducerRecord<String, String> record, Class<? extends CallbackPolicy> callbackPolicyClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends CallbackPolicy> constructor = callbackPolicyClass.getConstructor(this.getClass(), Producer.class, ProducerRecord.class);
        CallbackPolicy callbackPolicy = constructor.newInstance(this, producer, record);
        producer.send(record, callbackPolicy);
    }


    /**
     * 回调策略
     * @author haisenbao
     * @date  2020/5/12
     */
    @Data
    abstract class CallbackPolicy implements Callback {

        private Producer<String, String> producer;
        private ProducerRecord<String, String> record;

        CallbackPolicy(Producer<String, String> producer, ProducerRecord<String, String> record) {
            this.producer = producer;
            this.record = record;
        }
    }

    /**
     * 回调策略-记日志
     * @author haisenbao
     * @date  2020/5/12
     */
    public class LogCallbackPolicy extends CallbackPolicy {

        public LogCallbackPolicy (Producer<String, String> producer, ProducerRecord<String, String> record) {
            super(producer, record);
        }

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception == null) {
                log.info("send success: record={}, metadata={}", this.getRecord(), metadata);
            }else {
                log.error("send failed: record={}", this.getRecord(), exception);
            }
        }
    }

    /**
     * 回调策略-重新投递
     * @author haisenbao
     * @date  2020/5/12
     */
    public class ResendCallbackPolicy extends CallbackPolicy {

        public ResendCallbackPolicy (Producer<String, String> producer, ProducerRecord<String, String> record) {
            super(producer, record);
        }

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception == null) {
                log.info("send success: record={}, metadata={}", this.getRecord(), metadata);
            }else {
                log.error("send failed: record={}", this.getRecord(), exception);
                this.getProducer().send(this.getRecord(), this);
            }
        }
    }
}
