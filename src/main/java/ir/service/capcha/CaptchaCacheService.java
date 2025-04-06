package ir.service.capcha;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CaptchaCacheService {

    @CachePut(value = "captchaCache", key = "#id")
    public String put(String id, String code) {
        return code;
    }

    @Cacheable(value = "captchaCache", key = "#id")
    public String get(String id) {
        return null;
    }

    @CacheEvict(value = "captchaCache", key = "#id")
    public void evict(String id) {
        // nothing
    }
}
