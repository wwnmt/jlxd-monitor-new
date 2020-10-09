package edu.nuaa.nettop.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-08-26
 * Time: 18:09
 */
@Slf4j
public class CommonUtils {

    private static final RedisTemplate<String, String> redisTemplate;

    static {
        redisTemplate = initRedisTemplate();
    }

    public static String md5(String value) {
        return DigestUtils.md5Hex(value).toUpperCase();
    }

    public static <K, V> V getOrCreate(K key, Map<K, V> map,
                                       Supplier<V> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }

    public static void storeToRedis(String k, String v) {
        redisTemplate.opsForValue().set(k, v);
    }

    public static String getFromRedis(String k) {
        return redisTemplate.opsForValue().get(k);
    }

    public static List<String> getListFromRedis(String k) {
        return redisTemplate.opsForList().range(k, 0, -1);
    }

    public static void delInRedis(String k) {
        redisTemplate.delete(k);
    }

    private static RedisTemplate<String, String> initRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);
        redisTemplate.setConnectionFactory(initRedisConnectionFactory());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private static RedisConnectionFactory initRedisConnectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(30);
        poolConfig.setMaxTotal(50);
        poolConfig.setMaxWaitMillis(3000);
        JedisConnectionFactory factory = new JedisConnectionFactory(poolConfig);
        RedisStandaloneConfiguration configuration = factory.getStandaloneConfiguration();
        assert configuration != null;
        configuration.setHostName("192.168.31.15");
        configuration.setPort(6379);
        configuration.setPassword("123456");
        return factory;
    }
}
