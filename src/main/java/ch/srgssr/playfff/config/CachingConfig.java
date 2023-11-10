package ch.srgssr.playfff.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@EnableCaching
public class CachingConfig {

  public static final String DeepLinkCacheName = "DeeplinkParsePlayUrlJSContent";

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(Collections.singletonList(
            new ConcurrentMapCache(DeepLinkCacheName)));
    return cacheManager;
  }
}
