# 【004】dubbo 服务发现2.md

## invoker

`org.apache.dubbo.config.ReferenceConfig`

ReferenceConfig.invoker = new AsyncToSyncInvoker(Invoker<T> invoker);

AsyncToSyncInvoker.invoker = new DubboInvoker();

DubboInvoker.clients = ExchangeClient.class = new HeaderExchangeClient();

HeaderExchangeClient.client = new NettyClient();
HeaderExchangeClient.channel = new HeaderExchangeChannel(this.client);

NettyClient.handler -> origin = new DecodeHandler();