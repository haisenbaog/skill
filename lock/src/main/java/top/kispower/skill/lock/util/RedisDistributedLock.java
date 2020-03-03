package top.kispower.skill.lock.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author haisenbao
 * @date 2020/2/26 23:36
 *
 * 难点：锁不设置过期时间，可能由于单点故障，导致其他服务器的线程永远无法取得该锁。
 *      锁设置过期时间，当一个持锁线程在完成业务处理前，锁已过期，即使避免掉误删其他线程的锁，但是同一时刻，可能有多个线程在进行业务处理。
 */
@Component
public class RedisDistributedLock {

    private static final ThreadLocal<String> threadMark = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
    private static final ThreadLocal<Map<String, Integer>> lockMap = ThreadLocal.withInitial(HashMap::new);
//    private static final Map<Long, Map<String, Integer>> lockMap2 = new ConcurrentHashMap<>();
//    private static final Map<Thread, Map<String, Integer>> lockMap3; // 线程无法被释放

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public Boolean tryLock(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, threadMark.get(), time, timeUnit);
    }

    // 可能误删其他线程的锁（先判断，后删除；如果在判断后删除前，该key过期且有其他线程设置了相同的key(获取了锁)，会误删其他线程的锁）
    // 解决办法1：让判断和删除作为一个原子性操作，用lua脚本的方式（虽然不会误删其他线程的锁，但是当该线程的锁过期时，其他线程也能获取该锁，也就是说同一时刻有多个线程持有该锁，在进行业务处理）
    // 解决办法2：保证线程在删除自己的锁(即key)之前，锁(即key)不会过期，用定时调度线程，定时刷新过期时间（问题1：网络问题导致无法刷新过期时间怎么办。问题2：业务处理时间过长(等待其他资源等)怎么办，即锁长时间不得释放）
    public Boolean unLock(String key) {
        if (threadMark.get().equals(redisTemplate.opsForValue().get(key))) {
            return redisTemplate.delete(key);
        }
        return false;
    }

    public Boolean tryAcquire(String key, long time, TimeUnit timeUnit) {
        Integer lockNum = lockMap.get().get(key);
        if (lockNum != null && lockNum > 0) {
            lockMap.get().put(key, lockNum + 1);
            return true;
        }
        Boolean success = tryLock(key, time, timeUnit);
        if (success != null && success) {
            lockMap.get().put(key, 1);
            return true;
        }
        return false;
    }

    // 可能误删其他线程的锁(锁过期后，其他线程取得该锁，该线程再去删除锁)
    public Boolean release(String key) {
        Integer lockNum = lockMap.get().get(key);
        if (lockNum == null || lockNum <= 0) {
            return false;
        }
        lockMap.get().put(key, --lockNum);
        if (lockNum == 0) {
            redisTemplate.delete(key);
        }
        return true;
    }
}
