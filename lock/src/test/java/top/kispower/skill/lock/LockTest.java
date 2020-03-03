package top.kispower.skill.lock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author haisenbao
 * @date 2020/3/3
 */
@SpringBootTest
public class LockTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void getAndSetValue() {
        String value = (String)redisTemplate.opsForValue().get("k1");
        System.err.println("value:" +value);
        redisTemplate.opsForValue().set("k1", "v1");
        value = (String)redisTemplate.opsForValue().get("k1");
        System.err.println("value:" +value);
    }
}
