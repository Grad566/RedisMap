package org.example.collection;

import redis.clients.jedis.Jedis;

import java.util.AbstractMap;
import java.util.Collection;
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
        return (int) jedis.dbSize();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return jedis.exists(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value.toString());
    }

    @Override
    public String get(Object key) {
        return jedis.get(key.toString());
    }

    @Override
    public String put(String key, String value) {
        var prev = get(key);
        jedis.set(key, value);
        return prev;
    }

    @Override
    public String remove(Object key) {
        var val = jedis.get(key.toString());

        if (val != null) {
            jedis.del(key.toString());
        }

        return val;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for (var entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        jedis.flushDB();
    }

    @Override
    public Set<String> keySet() {
        return jedis.keys("*");
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
