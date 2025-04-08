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
/*
@CachePut:
هدف: مقدار جدید را در کش ذخیره می‌کند
عملکرد: صرف نظر از وجود یا عدم وجود مقدار قبلی، مقدار جدید را در کش قرار می‌دهد و همیشه متد را اجرا می‌کند

@Cacheable
هدف: در صورت وجود مقدار در کش، آن را برگردانده و از اجرای متد جلوگیری می‌کند
عملکرد: اگر مقدار با کلید مورد نظر در کش موجود باشد، بدون اجرای بدنه متد، مقدار کش شده را برمی‌گرداند

@CacheEvict
هدف: حذف مقدار از کش
عملکرد: مقدار با کلید مشخص شده را از کش حذف می‌کند
*/

