# 【200】design - dubbo heartbeat.md

+ [Dubbo 的心跳设计](https://mp.weixin.qq.com/s/be3cY2migO8JHUJouYg09Q)

> 何为心跳机制？  
> 简单来讲就是客户端启动一个定时器用来**定时发送请求**，服务端接到请求进行响应，如果**多次没有接受到响应**，
> 那么客户端认为连接已经断开，可以断开半打开的连接或者进行重连处理

## TCP KeepAlive
- [(三)IM心跳机制之tcp协议KeepAlive和应用层心跳-为何不用KeepAlive？](https://zhuanlan.zhihu.com/p/27106530)

**优点：**  
使用起来简单，减少了应用层代码的复杂度，也会更节省流量。  
因为应用层的数据传输到TCP协议层时都会被加上额外的包头包尾，由TCP协议提供的检活，其发的探测包，理论上实现的会更精妙，耗费更少的流量。

**致命缺点：**  
1. keepAlive只能检测连接存活，而不能检测连接可用  
比如某台服务器因为某些原因导致负载超高，CPU满了，无法响应任何业务请求，**但是使用 TCP 探针则仍旧能够确定连接状态**，这就是典型的连接活着但业务提供方已死的状态。  
对客户端而言，这时的最好选择就是断线后重新连接其他服务器，而不是一直认为当前服务器是可用状态，一直向当前服务器发送些必然会失败的请求。

2. tcp 自动重传优先级高于keepalive  
如果tcp连接的另一端突然掉线，这个时候我们并不知道网络已经关闭。  
而此时，如果有发送数据失败，tcp会自动进行重传。  
**重传包的优先级高于keepalive的包**，那就意味着，我们的keepalive总是不能发送出去。  
而此时，我们也并不知道该连接已经出错而中断。在较长时间的重传失败之后，我们才会知道。

## dubbo heartbeat

- `org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeClient`: heartbeat-task, reconnect-task
- `org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeServer`: idle-close-task

### HeaderExchangeClient - HeartbeatTask
`HeaderExchangeClient#startHeartBeatTask(...)`:  
expression: `last-read || last-write`  
heartbeat: url.heartbeat | 60s  
HEARTBEAT_CHECK_TICK: 3 (固定)  
heartbeatTimeoutTick: heartbeat / HEARTBEAT_CHECK_TICK = 20s  
每heartbeatTimeoutTick(20s)检测1次heartbeat，若间隔大于heartbeat(60s)则发送1次心跳。  
（或者 netty IdleStateHandler）

`HeaderExchangeClient#startReconnectTask(...)`:  
expression: `init-connect || last-read`
idleTimeout: url.heartbeat.timeout | url.heartbeat * 3, and > heartbeat * 2  (ex. 60s * 3 = 180s)  
heartbeatTimeoutTick: idleTimeout / 3 = 60s  
每60s检测1次idle-timeout，若间隔大于idleTimeout(180s)则reconnect。

last-read update:  
- `HeartbeatHandler#connected()`
- `HeartbeatHandler#received()`

last-write update:
- `HeartbeatHandler#connected()`
- `HeartbeatHandler#sent()`

`HeartbeatHandler#connected()`:  
当调用 `Netty.channel#connect() -> NettyClient#doConnect()` 触发netty的调用栈从而调用 `channelActive() -> HeartbeatHandler#connected()`。

`HeartbeatHandler#sent()`:  
发送请求时调用到`NettyClientHandler#write()` 从而调用`HeartbeatHandler#sent()`

`HeartbeatHandler#received()`:  
接收请求时会调用。

`IdleStateHandler`:  
基于netty的`userEventTriggered`