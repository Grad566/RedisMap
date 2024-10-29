package org.example.collection;

import org.example.exception.RedisException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.resps.ScanResult;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisMap implements Map<String, String> {
    private final Jedis jedis;

    public RedisMap(Jedis jedis) {
        this.jedis = jedis;
    }
    @Override
    public int size() {
        try {
            return (int) jedis.dbSize();
        } catch (JedisConnectionException e) {
            throw new RedisException("Error getting database size", e);
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            return jedis.exists(key.toString());
        } catch (JedisConnectionException  e) {
            throw new RedisException("Error checking existence of key: " + key, e);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value.toString());
    }

    @Override
    public String get(Object key) {
        try {
            return jedis.get(key.toString());
        } catch (JedisConnectionException e) {
            throw new RedisException("Error getting value for key: " + key, e);
        }
    }

    @Override
    public String put(String key, String value) {
        try {
            String prev = get(key);
            String res = jedis.set(key, value);

            if ("OK".equals(res)) {
                return prev;
            } else {
                throw new RedisException("Failed to set value for key: " + key);
            }

        } catch (JedisConnectionException  e) {
            throw new RedisException("Error setting value for key: " + key, e);
        }
    }

    @Override
    public String remove(Object key) {
        try {
            String val = get(key);
            long res = jedis.del(key.toString());
            return res == 1 ? val : "-1";
        } catch (JedisConnectionException e) {
            throw new RedisException("Error removing key: " + key, e);
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for (var entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        try {
            jedis.flushDB();
        } catch (JedisConnectionException e) {
            throw new RedisException("Error clearing the database", e);
        }
    }

    @Override
    public Set<String> keySet() {
        try {
            Set<String> keys = new HashSet<>();
            String cursor = "0";

            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                keys.addAll(scanResult.getResult());
                cursor = scanResult.getCursor();
            } while (!cursor.equals("0"));

            return keys;
        } catch (JedisConnectionException  e) {
            throw new RedisException("Error getting key set", e);
        }
    }

    @Override
    public Collection<String> values() {
        return keySet().stream().map(this::get).toList();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return keySet().stream().map(k -> new AbstractMap.SimpleEntry<String, String>(k, get(k)))
                .collect(Collectors.toSet());
    }
}
