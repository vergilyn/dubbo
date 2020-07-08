//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.dubbo.common.bytecode;

import java.lang.reflect.InvocationHandler;

import org.apache.dubbo.common.bytecode.ClassGenerator.DC;
import org.apache.dubbo.rpc.proxy.javassist.JavassistProxyFactory;

/**
 * IMPORTANT:
 *  1. classname实际是"Proxy0"，与之对应的是"proxy0"!
 *
 * <p> classname: Proxy{id} -> id = {@link Proxy.PROXY_CLASS_COUNTER#getAndIncrement()};
 *
 * @see Proxy#getProxy(Class[])
 * @see proxy0
 * @see `Proxy0a.class`
 */
public class Proxy0a extends Proxy implements DC {

    /**
     * <strong>`InvocationHandler` 实际是:</strong>
     * <pre>
     *     -> {@linkplain JavassistProxyFactory#getProxy(org.apache.dubbo.rpc.Invoker, java.lang.Class[])}
     *     -> {@linkplain org.apache.dubbo.rpc.proxy.InvokerInvocationHandler}
     * </pre>
     */
    public Object newInstance(InvocationHandler var1) {
        return new proxy0(var1);
    }

    public Proxy0a() {
    }
}
