//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.dubbo.common.bytecode;

import java.lang.reflect.InvocationHandler;

import org.apache.dubbo.rpc.proxy.javassist.JavassistProxyFactory;

/**
 * consumer 生成 {@linkplain com.vergilyn.examples.api.ProviderFirstApi} 代理类。
 * <pre>
 *   -> {@linkplain JavassistProxyFactory#getProxy(org.apache.dubbo.rpc.Invoker, java.lang.Class[])}
 *   -> {@linkplain Proxy#getProxy(java.lang.ClassLoader, java.lang.Class[])}
 * </pre>
 *
 * <p> classname: proxy{id} -> id = {@link Proxy.PROXY_CLASS_COUNTER#getAndIncrement()};
 *
 * @see Proxy#getProxy(Class[])
 * @see `proxy0.class`
 */
public class proxy0
        implements org.apache.dubbo.common.bytecode.ClassGenerator.DC,
        org.apache.dubbo.rpc.service.Destroyable,
        com.alibaba.dubbo.rpc.service.EchoService,
        com.vergilyn.examples.api.ProviderFirstApi {

    // 所有method，即依序是：["$destroy()", "$echo(...)", "sayGoodbye(...)", "sayHello(...)"]
    public static java.lang.reflect.Method[] methods;

    /**
     * <strong>handler 对象实际是:</strong>
     * <pre>
     *     -> {@linkplain JavassistProxyFactory#getProxy(org.apache.dubbo.rpc.Invoker, java.lang.Class[])}
     *     -> {@linkplain org.apache.dubbo.rpc.proxy.InvokerInvocationHandler}
     * </pre>
     */
    private java.lang.reflect.InvocationHandler handler;

    // from Destroyable.class
    @Override
    public void $destroy() {
        Object[] var1 = new Object[0];
        this.handler.invoke(this, methods[0], var1);
    }

    // from EchoService.class
    @Override
    public Object $echo(Object var1) {
        Object[] var2 = new Object[]{var1};
        Object var3 = this.handler.invoke(this, methods[1], var2);
        return (Object)var3;
    }

    // from ProviderFirstApi.class
    @Override
    public String sayGoodbye(String var1, long var2) {
        Object[] var4 = new Object[]{var1, new Long(var2)};
        Object var5 = this.handler.invoke(this, methods[2], var4);
        return (String)var5;
    }

    // from ProviderFirstApi.class
    @Override
    public String sayHello(String var1, long var2) {
        Object[] var4 = new Object[]{var1, new Long(var2)};
        Object var5 = this.handler.invoke(this, methods[3], var4);
        return (String)var5;
    }

    public proxy0() {
    }

    public proxy0(InvocationHandler var1) {
        this.handler = var1;
    }
}
