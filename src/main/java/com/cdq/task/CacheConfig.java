package com.cdq.task;

import javax.cache.spi.CachingProvider;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ehcache.config.units.MemoryUnit;
import javax.cache.CacheManager;
import javax.cache.Caching;
import java.time.Duration;

import com.cdq.task.models.Task;

@Configuration
public class CacheConfig {

    private static final String TASKS_CACHE = "tasks";

    @Bean
    public CacheManager ehcacheManager() {

        CacheConfiguration<Integer, Task> cachecConfig = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class,
                        Task.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .offheap(10, MemoryUnit.MB)
                                .build())
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(10)))
                .build();

        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();

        javax.cache.configuration.Configuration<Integer, Task> configuration = Eh107Configuration
                .fromEhcacheCacheConfiguration(cachecConfig);
        if (cacheManager.getCache(TASKS_CACHE, Integer.class, Task.class) == null) {
            cacheManager.createCache(TASKS_CACHE, configuration);
        }
        return cacheManager;
    }
}
