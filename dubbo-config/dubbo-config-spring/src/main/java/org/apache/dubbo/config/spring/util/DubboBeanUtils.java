/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config.spring.util;

import com.alibaba.spring.util.BeanRegistrar;

import org.apache.dubbo.config.spring.beans.factory.annotation.DubboConfigAliasPostProcessor;
import org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.apache.dubbo.config.spring.beans.factory.config.DubboConfigDefaultPropertyValueBeanPostProcessor;
import org.apache.dubbo.config.spring.context.DubboBootstrapApplicationListener;
import org.apache.dubbo.config.spring.context.DubboLifecycleComponentApplicationListener;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Dubbo Bean utilities class
 *
 * @since 2.7.6
 */
public interface DubboBeanUtils {

    /**
     * Register the common beans
     * <p>vergilyn-comment, 2020-03-09 >>>> <br/>
     *   {@linkplain EnableDubboConfig}、{@linkplain DubboComponentScan}都会调用到本方法。
     *   不会造成 repeat-registry，参加源码{@linkplain BeanRegistrar#registerInfrastructureBean(BeanDefinitionRegistry, String, Class)}。
     *
     *   <br/> 只是registry-bean，并未实例化（即未调用 constructor）！！！
     * </p>
     * @param registry {@link BeanDefinitionRegistry}
     * @see ReferenceAnnotationBeanPostProcessor
     * @see DubboConfigDefaultPropertyValueBeanPostProcessor
     * @see DubboConfigAliasPostProcessor
     * @see DubboLifecycleComponentApplicationListener
     * @see DubboBootstrapApplicationListener
     */
    static void registerCommonBeans(BeanDefinitionRegistry registry) {

        // Since 2.5.7 Register @Reference Annotation Bean Processor as an infrastructure Bean
        BeanRegistrar.registerInfrastructureBean(registry, ReferenceAnnotationBeanPostProcessor.BEAN_NAME,
                ReferenceAnnotationBeanPostProcessor.class);

        // Since 2.7.4 [Feature] https://github.com/apache/dubbo/issues/5093
        BeanRegistrar.registerInfrastructureBean(registry, DubboConfigAliasPostProcessor.BEAN_NAME,
                DubboConfigAliasPostProcessor.class);

        // Since 2.7.5 Register DubboLifecycleComponentApplicationListener as an infrastructure Bean
        BeanRegistrar.registerInfrastructureBean(registry, DubboLifecycleComponentApplicationListener.BEAN_NAME,
                DubboLifecycleComponentApplicationListener.class);

        // Since 2.7.4 Register DubboBootstrapApplicationListener as an infrastructure Bean
        BeanRegistrar.registerInfrastructureBean(registry, DubboBootstrapApplicationListener.BEAN_NAME,
                DubboBootstrapApplicationListener.class);


        // Since 2.7.6 Register DubboConfigDefaultPropertyValueBeanPostProcessor as an infrastructure Bean
        BeanRegistrar.registerInfrastructureBean(registry, DubboConfigDefaultPropertyValueBeanPostProcessor.BEAN_NAME,
                DubboConfigDefaultPropertyValueBeanPostProcessor.class);
    }
}
