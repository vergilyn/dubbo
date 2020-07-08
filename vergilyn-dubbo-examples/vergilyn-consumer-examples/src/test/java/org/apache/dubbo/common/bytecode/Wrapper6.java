//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.dubbo.common.bytecode;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.vergilyn.examples.api.ProviderFirstApi;

import org.apache.dubbo.common.bytecode.ClassGenerator.DC;

/**
 * provider 生成 {@linkplain ProviderFirstApi} 的包装类。
 * <p>
 *   classname: Wrapper{id} -> id = {@link org.apache.dubbo.common.bytecode.Wrapper.WRAPPER_CLASS_COUNTER#getAndIncrement()};
 *
 * @see Wrapper#makeWrapper(java.lang.Class)
 * @see `Wrapper6.class`
 */
public class Wrapper6 extends Wrapper implements DC {
    // property name array.
    public static String[] pns;     // new String[0]

    // property type map, <property name, property types>
    public static Map pts;          // Map<String, Class<?>> pts = new HashMap<>();

    // method names.
    public static String[] mns;     // new String[2]{"sayHello", "sayGoodbye"}
    // declaring method names.
    public static String[] dmns;    // new String[2]{"sayHello", "sayGoodbye"}

    // 方法对应的 parameter-types， mts[i] 指的就是 dms[i]这个方法的方法参数
    public static Class[] mts0;     // new Class[2]{String.class, long}
    public static Class[] mts1;     // new Class[2]{String.class, long}
    // public static Class[] mts{0...N};

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
            if ("sayHello".equals(var2) && var3.length == 2) {
                return var5.sayHello((String)var4[0], ((Number)var4[1]).longValue());
            }

            if ("sayGoodbye".equals(var2) && var3.length == 2) {
                return var5.sayGoodbye((String)var4[0], ((Number)var4[1]).longValue());
            }
        } catch (Throwable var9) {
            throw new InvocationTargetException(var9);
        }

        throw new NoSuchMethodException("Not found method \"" + var2 + "\" in class com.vergilyn.examples.api.ProviderFirstApi.");
    }

    public Wrapper6() {
    }
}
