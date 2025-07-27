package com.github.raftimpl.raft.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 分布式存储系统RESTful API服务
 * 
 * @author raft-java
 */
@SpringBootApplication(exclude = {
    org.redisson.spring.starter.RedissonAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class
})
@EnableCaching
@EnableScheduling
@ComponentScan(basePackages = "com.github.raftimpl.raft.api", 
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, 
            pattern = "com\\.github\\.raftimpl\\.raft\\.api\\.service\\.ai\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, 
            pattern = "com\\.github\\.raftimpl\\.raft\\.api\\.service\\.RedisSentinelHealthService"),
        @ComponentScan.Filter(type = FilterType.REGEX, 
            pattern = "com\\.github\\.raftimpl\\.raft\\.api\\.service\\.SmartCacheService"),
        @ComponentScan.Filter(type = FilterType.REGEX, 
            pattern = "com\\.github\\.raftimpl\\.raft\\.api\\.service\\.CacheConsistencyService"),
        @ComponentScan.Filter(type = FilterType.REGEX, 
            pattern = "com\\.github\\.raftimpl\\.raft\\.api\\.service\\.RateLimitService")
    })
public class RaftApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaftApiApplication.class, args);
    }
} 