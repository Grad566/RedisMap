package org.example.collection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Перед запуском тестов, нужно запустить docker-compose в фоновом режиме:
 * docker-compose up -d
 * После тестов:
 * docker-compose down
 */
class RedisMapTest {
    private static Jedis jedis;
    private static RedisMap redisMap;

    @BeforeAll
    public static void setUp() {
        jedis = new Jedis("localhost", 6379);
        redisMap = new RedisMap(jedis);
    }

    @AfterAll
    public static void shutDown() {
        jedis.flushDB();
        jedis.close();
    }

    @Test
    public void testSize() {
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");
        assertEquals(redisMap.size(), 2);
    }

    @Test
    public void testIsEmpty() {
        redisMap.clear();
        assertTrue(redisMap.isEmpty());
        redisMap.put("key1", "value1");
        assertFalse(redisMap.isEmpty());
    }

    @Test
    public void testContainsKey() {
        redisMap.put("key1", "value1");
        assertTrue(redisMap.containsKey("key1"));
        assertFalse(redisMap.containsKey("key5"));
    }

    @Test
    public void testContainsValue() {
        redisMap.put("key1", "value1");
        assertTrue(redisMap.containsValue("value1"));
        assertFalse(redisMap.containsValue("value6"));
    }

    @Test
    public void testPutAndGet() {
        redisMap.put("key1", "value1");
        assertEquals("value1", redisMap.get("key1"));
    }


    @Test
    public void testRemove() {
        redisMap.put("key1", "value1");
        var removedVal = redisMap.remove("key1");
        assertEquals(removedVal, "value1");
        assertNull(redisMap.get("key1"));
    }

    @Test
    public void testPutAllAndSets() {
        var map = new HashMap<String, String>(Map.of("key1", "value1", "key2", "value2"));
        redisMap.putAll(map);
        var entrySet = redisMap.entrySet();
        var keySet = redisMap.keySet();
        var values = redisMap.values();
        assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>("key1", "value1")));
        assertTrue(keySet.contains("key1"));
        assertTrue(values.contains("value2"));
    }


}