# 【900】provider & consumer proxy-class.MD

`org.apache.dubbo.rpc.proxy.javassist.JavassistProxyFactory`


## service provider

## provider

T proxy: `class com.vergilyn.examples.service.impl.ProviderFirstApiImpl`
Class<T> type: `interface com.vergilyn.examples.api.ProviderFirstApi`
URL url: 
```text
injvm://127.0.0.1/com.vergilyn.examples.api.ProviderFirstApi
    ?anyhost=true
    &application=dubbo-provider-application
    &bind.ip=127.0.0.1
    &bind.port=20880
    &deprecated=false
    &dubbo=2.0.2
    &dynamic=true
    &generic=false
    &interface=com.vergilyn.examples.api.ProviderFirstApi
    &logger=log4j2
    &methods=sayHello,sayGoodbye
    &pid=8124
    &release=2.7.6.RELEASE
    &revision=1.0.0
    &side=provider
    &timeout=1000
    &timestamp=1586925317589
    &version=1.0.0
```

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.dubbo.common.bytecode;

import com.vergilyn.examples.api.ProviderFirstApi;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.apache.dubbo.common.bytecode.ClassGenerator.DC;

/**
 * classname: Wrapper{id} -> id = org.apache.dubbo.common.bytecode.Wrapper.WRAPPER_CLASS_COUNTER.getAndIncrement();
 */
public class Wrapper2 extends Wrapper implements DC {
    public static String[] pns;     // property name array.
    public static Map<String, Class<?>> pts;  // property type map. <property name, property types>
    public static String[] mns;     // all method name array.
    public static String[] dmns;    // declared method name array.
    public static Class[] mts0;     // 方法对应的 parameterTypes
    public static Class[] mts1;
    // public static Class[] mts{0..N};

    public Wrapper2() {
    }

    public String[] getPropertyNames() {
        return pns;
    }

    public boolean hasProperty(String var1) {
        return pts.containsKey(var1);
    }

    public Class getPropertyType(String var1) {
        return (Class)pts.get(var1);
    }

    public String[] getMethodNames() {
        return mns;
    }

    public String[] getDeclaredMethodNames() {
        return dmns;
    }

    public void setPropertyValue(Object var1, String var2, Object var3) {
        try {
            ProviderFirstApi var4 = (ProviderFirstApi)var1;
        } catch (Throwable var6) {
            throw new IllegalArgumentException(var6);
        }

        throw new NoSuchPropertyException("Not found property \"" + var2 + "\" field or setter method in class com.vergilyn.examples.api.ProviderFirstApi.");
    }

    public Object getPropertyValue(Object var1, String var2) {
        try {
            ProviderFirstApi var3 = (ProviderFirstApi)var1;
        } catch (Throwable var5) {
            throw new IllegalArgumentException(var5);
        }

        throw new NoSuchPropertyException("Not found property \"" + var2 + "\" field or setter method in class com.vergilyn.examples.api.ProviderFirstApi.");
    }

    /**
     * @param var1 bean
     * @param var2 method name
     * @param var3 method parameter-types
     * @param var4 parameter values
     */
    public Object invokeMethod(Object var1, String var2, Class[] var3, Object[] var4) throws InvocationTargetException {
        ProviderFirstApi var5;
        try {
            var5 = (ProviderFirstApi)var1;
        } catch (Throwable var8) {
            throw new IllegalArgumentException(var8);
        }

        try {
            if ("sayGoodbye".equals(var2) && var3.length == 1) {
                return var5.sayGoodbye((String)var4[0]);
            }

            if ("sayHello".equals(var2) && var3.length == 1) {
                return var5.sayHello((String)var4[0]);
            }
        } catch (Throwable var9) {
            throw new InvocationTargetException(var9);
        }

        throw new NoSuchMethodException("Not found method \"" + var2 + "\" in class com.vergilyn.examples.api.ProviderFirstApi.");
    }
}
```

`org.apache.dubbo.common.bytecode.Wrapper#makeWrapper(Class<?> c)` 生成class后，setup static field:
```text
pts = new HashMap<String, Class<?>>;
pns = new String[0];
mns = {"sayGoodbye", "sayHello"};
dmns = {"sayGoodbye", "sayHello"};
mts0 = {"class java.lang.String"};
mts1 = {"class java.lang.String"};
```

## service consumer
`Proxy#getProxy(...)` 中生成 service 的代理类（ccp、ccm）：
- org.apache.dubbo.config.ReferenceConfig#createProxy()
- org.apache.dubbo.rpc.proxy.javassist.JavassistProxyFactory#getProxy()
- org.apache.dubbo.common.bytecode.Proxy#getProxy()
- InvocationHandler -> org.apache.dubbo.rpc.proxy.InvokerInvocationHandler

class: ``
```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.dubbo.common.bytecode;

import com.alibaba.dubbo.rpc.service.EchoService;
import com.vergilyn.examples.api.ProviderFirstApi;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.apache.dubbo.common.bytecode.ClassGenerator.DC;
import org.apache.dubbo.rpc.service.Destroyable;

/**
 * {@linkplain javassist.CtClass#writeFile()} 输出class反编译成java 后二次调整后的代码
 * dubbo ccp
 * classname: proxy{id} -> id = `org.apache.dubbo.common.bytecode.Proxy.PROXY_CLASS_COUNTER#getAndIncrement()`
 */
public class proxy0 
    implements 
        org.apache.dubbo.common.bytecode.ClassGenerator.DC, 
        org.apache.dubbo.rpc.service.Destroyable, 
        com.alibaba.dubbo.rpc.service.EchoService, 
        com.vergilyn.examples.api.ProviderFirstApi {

    public static Method[] methods = {"$destroy", "sayHello", "sayGoodbye", "$echo"};
    
    // JavassistProxyFactory#getProxy() -> org.apache.dubbo.rpc.proxy.InvokerInvocationHandler
    private java.lang.reflect.InvocationHandler handler;

    public proxy0() {
    }

    public proxy0(java.lang.reflect.InvocationHandler var1) {
        this.handler = var1;
    }

    // Destroyable.java
    public void $destroy() {
        Object[] var1 = new Object[0];
        this.handler.invoke(this, methods[0], var1);
    }

    public String sayHello(String var1) {
        Object[] var2 = new Object[]{var1};
        Object var3 = this.handler.invoke(this, methods[1], var2);
        return (String)var3;
    }

    public String sayGoodbye(String var1) {
        Object[] var2 = new Object[]{var1};
        Object var3 = this.handler.invoke(this, methods[2], var2);
        return (String)var3;
    }

    @Override  // EchoService.java
    public Object $echo(Object var1) {
        Object[] var2 = new Object[]{var1};
        Object var3 = this.handler.invoke(this, methods[3], var2);
        return (Object)var3;
    }
}
```

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.dubbo.common.bytecode;

import java.lang.reflect.InvocationHandler;
import org.apache.dubbo.common.bytecode.ClassGenerator.DC;

// dubbo ccm, 与 dubbo-ccp生成的class一样，所以可以组合成一个class
public class Proxy0 
    extends org.apache.dubbo.common.bytecode.Proxy 
    implements org.apache.dubbo.common.bytecode.ClassGenerator.DC {

    @Override // Proxy.java
    public Object newInstance(java.lang.reflect.InvocationHandler var1) {
        return new org.apache.dubbo.common.bytecode.proxy0(var1);
    }

    public Proxy0() {
    }
}

```

**remark: ccp、ccm 最终可能是相同的 proxy-class，** EX. `org.apache.dubbo.common.bytecode.proxy0`**  

---

回到实际业务代码中，此时通过`@Reference`已经为service创建了proxy-class，并实例化后注入到了业务类中。
```java
@SpringBootApplication
@Slf4j
public class ConsumerExamplesApplication implements CommandLineRunner {

    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 500000, check = false)
    private ProviderFirstApi firstApi;
    @Reference(version = ApiConstants.SERVICE_VERSION, timeout = 500000, check = true)
    private ProviderSecondApi secondApi;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConsumerExamplesApplication.class);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(firstApi.sayHello("vergilyn"));
        log.info(secondApi.print("vergilyn"));

        log.info(">>>> finish <<<<");
    }
}
```

例如，`ProviderFirstApi` 的 proxy-class 即上面给出类似代码。
然后现在执行`firstApi.sayHello("vergilyn")`，实际就是调用的`proxy0#sayHello()`。

然后通过源码调用栈可知：  
1) handler 即`org.apache.dubbo.rpc.proxy.InvokerInvocationHandler`。  
2) methods\[1] 即 `sayHello()`。
3) args 即参数

现在转到代码`InvokerInvocationHandler#invoke()`。

