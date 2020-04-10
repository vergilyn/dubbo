# 【900】service-proxy-class.MD

- [javassist使用全解析](https://www.cnblogs.com/rickiyang/p/11336268.html)
+ [javassist tutorial2](http://www.javassist.org/tutorial/tutorial2.html)

<table border="0">
	<tbody>
		<tr>
			<td>
				<code>$0
				</code>, 
				<code>$1
				</code>, 
				<code>$2
				</code>, ... &nbsp; &nbsp;
			</td>
			<td>
				<code>this
				</code> and actual parameters
			</td>
		</tr>
		<tr>
			<td>
				<code>$args
				</code>
			</td>
			<td>An array of parameters.
				The type of 
				<code>$args
				</code> is 
				<code>Object[]
				</code>.
			</td>
		</tr>
		<tr>
			<td>
				<code>$$
				</code>
			</td>
			<td rowspan="2">All actual parameters.
				<br>
				For example, 
				<code>m($$)
				</code> is equivalent to
				<code>m($1,$2,
				</code>...
				<code>)
				</code>
			</td>
		</tr>
		<tr>
			<td>&nbsp;
			</td>
		</tr>
		<tr>
			<td>
				<code>$cflow(
				</code>...
				<code>)
				</code>
			</td>
			<td>
				<code>cflow
				</code> variable
			</td>
		</tr>
		<tr>
			<td>
				<code>$r
				</code>
			</td>
			<td>The result type.  It is used in a cast expression.
			</td>
		</tr>
		<tr>
			<td>
				<code>$w
				</code>
			</td>
			<td>The wrapper type.  It is used in a cast expression.
			</td>
		</tr>
		<tr>
			<td>
				<code>$_
				</code>
			</td>
			<td>The resulting value
			</td>
		</tr>
		<tr>
			<td>
				<code>$sig
				</code>
			</td>
			<td>An array of 
				<code>java.lang.Class
				</code> objects representing
				the formal parameter types.
			</td>
		</tr>
		<tr>
			<td>
				<code>$type
				</code>
			</td>
			<td>A 
				<code>java.lang.Class
				</code> object representing
				the formal result type.
			</td>
		</tr>
		<tr>
			<td>
				<code>$class
				</code>
			</td>
			<td>A 
				<code>java.lang.Class
				</code> object representing
				the class currently edited.
			</td>
		</tr>
	</tbody>
</table>


`Proxy#getProxy(...)` 中生成 service 的代理类（ccp、ccm）
- org.apache.dubbo.config.ReferenceConfig#createProxy()
- org.apache.dubbo.rpc.proxy.javassist.JavassistProxyFactory#getProxy()
- org.apache.dubbo.common.bytecode.Proxy#getProxy()
- InvocationHandler -> org.apache.dubbo.rpc.proxy.InvokerInvocationHandler

如果 proxy-class 不是 public，

```java
package org.apache.dubbo.common.bytecode;

// dubbo ccp, 代理类
// org.apache.dubbo.common.bytecode.ClassGenerator -> javassist
// ClassName: org.apache.dubbo.common.bytecode.proxy0
public class proxy0 implements 
        org.apache.dubbo.rpc.service.Destroyable, 
        com.alibaba.dubbo.rpc.service.EchoService, 
        com.vergilyn.examples.api.ProviderFirstApi {
    
    public static java.lang.reflect.Method[] methods = {"sayHello", "sayGoodbye", "$destroy", "$echo"};

    // JavassistProxyFactory#getProxy() -> org.apache.dubbo.rpc.proxy.InvokerInvocationHandler
    private java.lang.reflect.InvocationHandler handler;

    // 默认构造函数：`public <init>(){}` 
    public proxy0(){}

    /* 自定义构造函数：
     * public <init>(java.lang.reflect.InvocationHandler arg0){handler=$1;} 
     */
    public proxy0(java.lang.reflect.InvocationHandler arg0){
        handler=$1;
    }

    @Override
    public java.lang.String sayHello(java.lang.String arg0){
        Object[] args = new Object[1]; 
        args[0] = ($w)$1; 
        Object ret = handler.invoke(this, methods[0], args); 
        return (java.lang.String)ret;
    }
    
    @Override
    public java.lang.String sayGoodbye(java.lang.String arg0){
        Object[] args = new Object[1]; 
        args[0] = ($w)$1; 
        Object ret = handler.invoke(this, methods[1], args); 
        return (java.lang.String)ret;
    }
    
    @Override  // Destroyable.java
    public void $destroy(){
        Object[] args = new Object[0];
        Object ret = handler.invoke(this, methods[2], args);
    }
    
    @Override  // EchoService.java
    public java.lang.Object $echo(java.lang.Object arg0){
        Object[] args = new Object[1]; 
        args[0] = ($w)$1; 
        Object ret = handler.invoke(this, methods[3], args); 
        return (java.lang.Object)ret;
    }
}
```

```java
package org.apache.dubbo.common.bytecode;

// dubbo ccm
public class proxy0 extends 
        org.apache.dubbo.common.bytecode.Proxy {
    
    public proxy0(){}
    
    @Override  // Proxy.java
    public Object newInstance(java.lang.reflect.InvocationHandler h) {
       return new org.apache.dubbo.common.bytecode.proxy0($1);  // 即 cpp 创建的 proxy-class
    }
}
```

**remark:**  
1. ccp package-name, "non-public interfaces from different packages"

2. 因为proxy-class均是 public，所以 ccp、ccm 的最终 classname都是 "org.apache.dubbo.common.bytecode.proxy{0...N}"  
** ccp、ccm 最终形成一个 proxy-class，** EX. `org.apache.dubbo.common.bytecode.proxy0`

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
2) methods\[0] 即 `sayHello()`。
3) args 即参数

现在转到代码`InvokerInvocationHandler#invoke()`。


