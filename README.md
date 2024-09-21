## KV-хранилище RedisMap

### Требования для локального запуска
jdk - 21

gradle - 8.7

Docker

### RedisMap
Конструктор принимает Jedis.
```
jedis = new Jedis("localhost", 6379);
redisMap = new RedisMap(jedis);
```

### Запуск тестов
Перед запуском тестов, нужно запустить docker-compose:
- ```docker-compose up -d```

После можно запускать тесты:
- ``` ./gradlew test```

После тестов:
- ``` docker-compose down```
