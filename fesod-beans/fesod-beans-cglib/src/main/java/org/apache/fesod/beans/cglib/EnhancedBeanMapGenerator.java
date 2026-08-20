/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fesod.beans.cglib;

import java.security.ProtectionDomain;
import org.apache.fesod.shaded.asm.ClassVisitor;
import org.apache.fesod.shaded.cglib.beans.BeanMap;
import org.apache.fesod.shaded.cglib.core.AbstractClassGenerator;
import org.apache.fesod.shaded.cglib.core.KeyFactory;
import org.apache.fesod.shaded.cglib.core.ReflectUtils;

/**
 * Copied from {@link org.apache.fesod.shaded.cglib.beans.BeanMap.Generator},
 * with only the class generation delegated to {@link EnhancedBeanMapEmitter}.
 */
class EnhancedBeanMapGenerator extends AbstractClassGenerator {
    private static final Source SOURCE = new Source(BeanMap.class.getName());

    private static final BeanMapKey KEY_FACTORY =
            (BeanMapKey) KeyFactory.create(BeanMapKey.class, KeyFactory.CLASS_BY_NAME);

    interface BeanMapKey {
        public Object newInstance(Class type, int require);
    }

    private Object bean;
    private Class beanClass;
    private int require;

    public EnhancedBeanMapGenerator() {
        super(SOURCE);
    }

    /**
     * Set the bean that the generated map should reflect. The bean may be swapped
     * out for another bean of the same type using {@link #setBean}.
     * Calling this method overrides any value previously set using {@link #setBeanClass}.
     * You must call either this method or {@link #setBeanClass} before {@link #create}.
     *
     * @param bean the initial bean
     */
    public void setBean(Object bean) {
        this.bean = bean;
        if (bean != null) {
            beanClass = bean.getClass();
            // SPRING PATCH BEGIN
            setContextClass(beanClass);
            // SPRING PATCH END
        }
    }

    /**
     * Set the class of the bean that the generated map should support.
     * You must call either this method or {@link #setBeanClass} before {@link #create}.
     *
     * @param beanClass the class of the bean
     */
    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * Limit the properties reflected by the generated map.
     *
     * @param require any combination of {@link BeanMap#REQUIRE_GETTER} and
     *                {@link BeanMap#REQUIRE_SETTER}; default is zero (any property allowed)
     */
    public void setRequire(int require) {
        this.require = require;
    }

    @Override
    protected ClassLoader getDefaultClassLoader() {
        return beanClass.getClassLoader();
    }

    @Override
    protected ProtectionDomain getProtectionDomain() {
        return ReflectUtils.getProtectionDomain(beanClass);
    }

    /**
     * Create a new instance of the <code>BeanMap</code>. An existing
     * generated class will be reused if possible.
     */
    public BeanMap create() {
        if (beanClass == null) {
            throw new IllegalArgumentException("Class of bean unknown");
        }
        setNamePrefix(beanClass.getName());
        return (BeanMap) super.create(KEY_FACTORY.newInstance(beanClass, require));
    }

    @Override
    public void generateClass(ClassVisitor v) throws Exception {
        // APACHE FESOD PATCH BEGIN
        new EnhancedBeanMapEmitter(v, getClassName(), beanClass, require);
        // APACHE FESOD PATCH END
    }

    @Override
    protected Object firstInstance(Class type) {
        return ((BeanMap) ReflectUtils.newInstance(type)).newInstance(bean);
    }

    @Override
    protected Object nextInstance(Object instance) {
        return ((BeanMap) instance).newInstance(bean);
    }
}
