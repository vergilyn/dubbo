# 【403】feat - dubbo filter

+ [dubbo filter](http://dubbo.apache.org/zh-cn/blog/first-dubbo-filter.html)

- [责任链模式](https://www.runoob.com/design-pattern/chain-of-responsibility-pattern.html)

```text
## dubbo monitor 实现原理 （责任链模式）
dubbo-monitor-api，dubbo-monitor-default 具体实现可能需要参考 dubbo-admin。
Consumer 端在发起调用之前会先走 filter 链；
provider 端在接收到请求时也是先走 filter 链，
然后才进行真正的业务逻辑处理。

默认情况下，在 consumer 和 provider 的 filter 链中都会有 MonitorFilter。
1、MonitorFilter 向 DubboMonitor 发送数据

2、DubboMonitor 将数据进行聚合后（默认聚合 1min 中的统计数据）暂存到ConcurrentMap<Statistics, AtomicReference> statisticsMap，
然后使用一个含有 3 个线程（线程名字：DubboMonitorSendTimer）的线程池每隔 1min 钟，调用 SimpleMonitorService（MonitorService 的具体实现类） 
遍历发送statisticsMap 中的统计数据，每发送完毕一个，就重置当前的 Statistics 的AtomicReference

3、SimpleMonitorService 将这些聚合数据塞入 BlockingQueue queue 中（队列大写为 100000）

4、SimpleMonitorService 使用一个后台线程（线程名为：DubboMonitorAsyncWriteLogThread）
将queue 中的数据写入文件（该线程以死循环的形式来写）

5、SimpleMonitorService 还会使用一个含有 1 个线程（线程名字：DubboMonitorTimer）的线程池每隔 5min 钟，将文件中的统计数据画成图表
```

`ProtocolFilterWrapper#buildInvokerChain(...)` 中的 `filters`:  
 
```text
## provider-side
- 0, org.apache.dubbo.rpc.filter.EchoFilter
- 1, org.apache.dubbo.rpc.filter.ClassLoaderFilter
- 2, org.apache.dubbo.rpc.filter.GenericFilter
- 3, org.apache.dubbo.rpc.filter.ContextFilter
- 4, org.apache.dubbo.rpc.protocol.dubbo.filter.TraceFilter
- 5, org.apache.dubbo.rpc.filter.TimeoutFilter
- 6, org.apache.dubbo.monitor.support.MonitorFilter
- 7, org.apache.dubbo.rpc.filter.ExceptionFilter

## consumer-side
- 0, org.apache.dubbo.rpc.filter.ConsumerContextFilter
- 1, org.apache.dubbo.rpc.protocol.dubbo.filter.FutureFilter
- 2, org.apache.dubbo.monitor.support.MonitorFilter
```

以 consumer-side 为例，filter 调用顺序是：
consumer -> future -> monitor -> invoker（真正的service-proxy）
