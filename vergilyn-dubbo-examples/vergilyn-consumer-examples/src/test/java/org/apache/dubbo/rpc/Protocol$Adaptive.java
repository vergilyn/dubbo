package org.apache.dubbo.rpc;

import org.apache.dubbo.common.compiler.support.AdaptiveCompiler;
import org.apache.dubbo.common.compiler.support.JavassistCompiler;
import org.apache.dubbo.common.extension.AdaptiveClassCodeGenerator;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ReferenceConfig;

/**
 * <pre>
 *   {@link ReferenceConfig#REF_PROTOCOL}
 *   -> {@link ExtensionLoader#createAdaptiveExtensionClass()}
 *     -<strong>code</strong>-> {@link AdaptiveClassCodeGenerator#generate()}, 即本java类
 *   -> {@link AdaptiveCompiler#compile(java.lang.String, java.lang.ClassLoader)}
 *   -> {@link JavassistCompiler#compile(String code, ClassLoader classLoader)}
 *   -> {@link JavassistCompiler#doCompile(String, String)}
 *     --> javassist CtClass
 * </pre>
 *
 * javassist 根据 <strong>code</strong> 生成的class -> `Protocol$Adaptive.class`。
 * <br/>（反编译后与本java代码一样）
 * @author vergilyn
 * @date 2020-04-21
 */
public class Protocol$Adaptive implements org.apache.dubbo.rpc.Protocol {

    @Override
    public org.apache.dubbo.rpc.Exporter export(org.apache.dubbo.rpc.Invoker arg0) throws org.apache.dubbo.rpc.RpcException {
        if (arg0 == null) throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument == null");

        if (arg0.getUrl() == null)
            throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument getUrl() == null");

        org.apache.dubbo.common.URL url = arg0.getUrl();
        String extName = (url.getProtocol() == null ? "dubbo" : url.getProtocol());
        if (extName == null)
            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.rpc.Protocol) name from url (" + url.toString() + ") use keys([protocol])");

        org.apache.dubbo.rpc.Protocol extension =
                (org.apache.dubbo.rpc.Protocol) ExtensionLoader.getExtensionLoader(org.apache.dubbo.rpc.Protocol.class).getExtension(extName);
        return extension.export(arg0);
    }

    @Override
    public org.apache.dubbo.rpc.Invoker refer(java.lang.Class arg0, org.apache.dubbo.common.URL arg1) throws org.apache.dubbo.rpc.RpcException {
        if (arg1 == null) throw new IllegalArgumentException("url == null");

        org.apache.dubbo.common.URL url = arg1;
        String extName = (url.getProtocol() == null ? "dubbo" : url.getProtocol());
        if (extName == null)
            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.rpc.Protocol) name from url (" + url.toString() + ") use keys([protocol])");

        org.apache.dubbo.rpc.Protocol extension =
                (org.apache.dubbo.rpc.Protocol) ExtensionLoader.getExtensionLoader(org.apache.dubbo.rpc.Protocol.class).getExtension(extName);
        return extension.refer(arg0, arg1);
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("The method public abstract void org.apache.dubbo.rpc.Protocol.destroy() "
                + "of interface org.apache.dubbo.rpc.Protocol is not adaptive method!");
    }

    @Override
    public int getDefaultPort() {
        throw new UnsupportedOperationException("The method public abstract int org.apache.dubbo.rpc.Protocol.getDefaultPort() "
                + "of interface org.apache.dubbo.rpc.Protocol is not adaptive method!");
    }

    @Override
    public java.util.List getServers() {
        throw new UnsupportedOperationException("The method public default java.util.List org.apache.dubbo.rpc.Protocol.getServers() "
                + "of interface org.apache.dubbo.rpc.Protocol is not adaptive method!");
    }
}