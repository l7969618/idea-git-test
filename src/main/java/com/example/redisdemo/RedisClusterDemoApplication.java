package com.example.redisdemo;

import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class RedisClusterDemoApplication {

    private static final Logger log = LoggerFactory.getLogger(RedisClusterDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RedisClusterDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner redisConnectivityCheck(StringRedisTemplate redisTemplate) {
        return args -> {
            log.info("Starting Redis cluster connectivity check...");

            try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
                String ping = connection.ping();
                log.info("PING response: {}", ping);
            }

            String key = "demo:connectivity:" + UUID.randomUUID();
            String value = "hello-gcp-redis-" + Instant.now();

            redisTemplate.opsForValue().set(key, value);
            String storedValue = redisTemplate.opsForValue().get(key);
            log.info("SET/GET test: key={} value={} storedValue={}", key, value, storedValue);

            String counterKey = "demo:counter";
            Long incremented = redisTemplate.opsForValue().increment(counterKey);
            log.info("INCR test: key={} value={}", counterKey, incremented);

            String hashKey = "demo:hash";
            redisTemplate.opsForHash().put(hashKey, "lastRun", Instant.now().toString());
            Object hashValue = redisTemplate.opsForHash().get(hashKey, "lastRun");
            log.info("HASH test: key={} field=lastRun value={}", hashKey, hashValue);

            log.info("Redis cluster connectivity check completed.");
        };
    }
}
