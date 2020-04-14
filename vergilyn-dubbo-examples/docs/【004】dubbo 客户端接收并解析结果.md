# 【004】dubbo 客户端接收并解析结果.md

通过之前的跟踪可知：  
![](./plant-uml/dubbo_service_refer_track.png)

由netty可知，netty双向传递调用的是 `NettyClientHandler#channelRead`