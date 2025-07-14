package org.example.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import org.apache.logging.log4j.Logger;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Connection;
import redis.clients.jedis.JedisPooled;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Type;

public class RedisService {
    private static final Logger LOG = LogManager.getLogger(RedisService.class);
    private static final JedisPooled jedisPooled;
    private static final Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER).create();
    private static final int EXPIRE_TIME = 8 * 3600; // 8 hour

    static {
        LOG.info("=============Init Redis Service==============");
        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(32);
        poolConfig.setMinIdle(8);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setBlockWhenExhausted(true);
        jedisPooled = new JedisPooled(poolConfig, "localhost", 6379,"root", "Huythai-27112004");
    }
    public static JedisPooled getRedisPooled() {
        return jedisPooled;
    }

    public static <T> T get (String key, Class<T> classOfT) {
        try {
            String value = jedisPooled.get(key);
            if (value != null) {
                return gson.fromJson(value, classOfT);
            }
        } catch (Exception e) {
            LOG.error("Redis get error for key {}: {}",key, e.getMessage(), e);
        }
        return  null;
    }

    public static <T> T get(String key, Type typeOfT) {
        try {
            String value = jedisPooled.get(key);
            if (value != null) {
                return gson.fromJson(value, typeOfT);
            }
        } catch (Exception e) {
            LOG.error("Redis get error for key {}: {}", key, e.getMessage(), e);
        }
        return null;
    }

    public static void set(String key, Object value) {
        try {
            String jsonValue = gson.toJson(value);
            jedisPooled.setex(key, EXPIRE_TIME, jsonValue);
        } catch (Exception e) {
            LOG.error("Redis set error for key {}: {}", key, e.getMessage(), e);
        }
    }

    public static void setWithoutExpiry (String key, Object value) {
        try {
            String jsonValue = gson.toJson(value);
            jedisPooled.set(key, jsonValue);
        } catch (Exception e) {
            LOG.error("Redis set error: {}" , e.getMessage(), e);
        }
    }

}
