package br.com.henry.selective.uol.customer.config;

import br.com.henry.selective.uol.customer.support.GracefulCacheErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    public static final String CACHE_KEY_PREFIX = "uolcusapi";

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(@Value("${redis.hostname:redis}") String hostName, @Value("${redis.port:6379}") Integer port) {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(hostName, port));
    }

    @Bean
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ZERO)
            .prefixKeysWith(CACHE_KEY_PREFIX)
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory)
            .cacheDefaults(redisCacheConfiguration).build();
    }

    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        return new GracefulCacheErrorHandler();
    }
}