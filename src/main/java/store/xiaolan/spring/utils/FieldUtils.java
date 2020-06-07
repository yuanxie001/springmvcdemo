package store.xiaolan.spring.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import store.xiaolan.spring.domian.Order;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldUtils {
    /**
     * 移除制定名称的参数值.
     *
     * @param obj
     */
    public static void removeFiledValue(Object obj, Set<String> fieldNameSet) {
        if (obj == null || fieldNameSet == null){
            return;
        }
        // 获取包装类型
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        // 获取属性编辑器
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        // 对每个属性数据的处理.
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            if (!propertyType.isPrimitive() && fieldNameSet.contains(propertyDescriptor.getName())) {
                // 对包装器的操作都会反映在传入的实体类上.
                beanWrapper.setPropertyValue(propertyDescriptor.getName(), null);
            }

        }
    }



}
