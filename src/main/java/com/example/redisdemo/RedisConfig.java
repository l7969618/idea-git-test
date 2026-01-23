package com.example.redisdemo;

import java.time.Duration;
import java.util.List;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(clusterNodes(properties));
        if (properties.getCluster() != null && properties.getCluster().getMaxRedirects() != null) {
            clusterConfiguration.setMaxRedirects(properties.getCluster().getMaxRedirects());
        }
        if (StringUtils.hasText(properties.getPassword())) {
            clusterConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
        }

        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .commandTimeout(timeoutOrDefault(properties.getTimeout()))
                .shutdownTimeout(timeoutOrDefault(properties.getLettuce().getShutdownTimeout()))
                .poolConfig(poolConfig(properties.getLettuce().getPool()))
                .build();

        return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    private List<String> clusterNodes(RedisProperties properties) {
        if (properties.getCluster() == null || properties.getCluster().getNodes() == null) {
            throw new IllegalStateException("Redis cluster nodes must be configured.");
        }
        return properties.getCluster().getNodes();
    }

    private GenericObjectPoolConfig<?> poolConfig(RedisProperties.Pool pool) {
        GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
        if (pool == null) {
            return config;
        }
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getMaxWait() != null) {
            config.setMaxWait(pool.getMaxWait());
        }
        return config;
    }

    private Duration timeoutOrDefault(Duration duration) {
        return duration != null ? duration : Duration.ofSeconds(60);
    }
}
