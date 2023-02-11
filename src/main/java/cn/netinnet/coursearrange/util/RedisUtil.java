package cn.netinnet.coursearrange.util;

import cn.netinnet.coursearrange.exception.ServiceException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisUtil {


    public static RedisTemplate<String, Object> redisTemplate;

    /** 静态变量通过set方法注入 */
    @Resource
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        RedisUtil.redisTemplate = redisTemplate;
    }

    /** 默认有效：1天 */
    private static long DEFAULT_CACHE_EXPIRE = 60 * 24;
    /** 默认单位：分 */
    private static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    public static void set(String key, Object value) {
        set(key, value, DEFAULT_CACHE_EXPIRE, DEFAULT_TIME_UNIT);
    }

    public static void set(String key, Object value, Long expire) {
        set(key, value, expire, DEFAULT_TIME_UNIT);
    }

    public static void set(String key, Object value, Long expire, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, expire, timeUnit);
        } catch (Exception e) {
            throw new ServiceException(-1, "redis缓存set失败!");
        }
    }

    public static String getString(String key) {
        try {
            return (String) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new ServiceException(-1, "redis缓存get失败!");
        }
    }

    public static <T> T get(String key) {
        try {
            return (T) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new ServiceException(-1, "redis缓存get失败!");
        }
    }

    public static void del(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new ServiceException(-1, "redis缓存del失败!");
        }
    }

}
