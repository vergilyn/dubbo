# feat - dubbo cache

+ [dubbo 结果缓存](http://dubbo.apache.org/zh-cn/docs/user/demos/result-cache.html)
+ [dubbo 缓存扩展](http://dubbo.apache.org/zh-cn/docs/dev/impls/cache.html)

core code:
- **`org.apache.dubbo.cache.filter.CacheFilter CacheFilter`**

- `org.apache.dubbo.cache.CacheFactory`
- `org.apache.dubbo.cache.support.jcache.JCacheFactory`
- `org.apache.dubbo.cache.support.lru.LruCacheFactory`
- `org.apache.dubbo.cache.support.threadlocal.ThreadLocalCacheFactory`
- `org.apache.dubbo.cache.support.expiring.ExpiringCacheFactory`

2020-05-14 >>>>  
**dubbo cache支持的功能也是超级简单！！！**  
可能需要扩展缓存。  
