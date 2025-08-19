package cn.idev.excel.util;

import cn.idev.excel.support.cglib.beans.BeanMap;
import cn.idev.excel.support.cglib.core.DefaultNamingPolicy;

/**
 * bean utils
 *
 *
 */
public class BeanMapUtils {

    /**
     * Helper method to create a new <code>BeanMap</code>.  For finer
     * control over the generated instance, use a new instance of
     * <code>BeanMap.Generator</code> instead of this static method.
     *
     * Custom naming policy to prevent null pointer exceptions.
     *
     * @param bean the JavaBean underlying the map
     * @return a new <code>BeanMap</code> instance
     */
    public static BeanMap create(Object bean) {
        BeanMap.Generator gen = new BeanMap.Generator();
        gen.setBean(bean);
        gen.setContextClass(bean.getClass());
        gen.setNamingPolicy(FastExcelNamingPolicy.INSTANCE);
        return gen.create();
    }

    public static class FastExcelNamingPolicy extends DefaultNamingPolicy {
        public static final FastExcelNamingPolicy INSTANCE = new FastExcelNamingPolicy();

        @Override
        protected String getTag() {
            return "ByFastExcelCGLIB";
        }
    }
}
