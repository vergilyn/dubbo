# issues#6152 - consumer generic exception
> 泛化接口调用方式主要用于客户端没有 API 接口及模型类元的情况，参数及返回值中的所有 POJO 均用 Map 表示，  
> 通常用于框架集成，比如：实现一个通用的服务测试框架，可通过 GenericService 调用所有服务实现。

docs:  
+ [dubbo 使用泛化调用](http://dubbo.apache.org/zh-cn/docs/user/demos/generic-reference.html)
+ [dubbo 实现泛化调用](http://dubbo.apache.org/zh-cn/docs/user/demos/generic-service.html)


issues:  
+ [issues#6152, dubbo consumer 端泛化调用provider端接口时，报 ClassNotFoundException 异常](https://github.com/apache/dubbo/issues/6152)
+ [issues#6112, GenericService Invoke Don't Support Subclasses](https://github.com/apache/dubbo/issues/6112)