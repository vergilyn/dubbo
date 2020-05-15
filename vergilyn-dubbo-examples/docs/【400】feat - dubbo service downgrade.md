# 【400】feat - dubbo service downgrade.md

+ [dubbo 服务降级](http://dubbo.apache.org/zh-cn/docs/user/demos/service-downgrade.html)

- [dubbo的Mock功能与源码实现](https://www.jianshu.com/p/ce8de35986c)

core code: `org.apache.dubbo.rpc.support.MockInvoker`

Normalize mock string:
<ol>
<li>return => return null</li>
<li>fail => default</li>
<li>force => default</li>
<li>fail:throw/return foo => throw/return foo</li>
<li>force:throw/return foo => throw/return foo</li>
</ol>

2020-05-14 >>>>  
**dubbo 服务降级支持的功能超级简单！！！**