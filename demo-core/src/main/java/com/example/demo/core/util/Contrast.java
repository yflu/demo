package com.example.demo.core.util;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.core.common.model.aop.ContrastBean;
import com.example.demo.core.common.model.aop.ContrastResultBean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 对比两个对象的变化的工具类
 *
 * @author eric
 * @Date 2019/1/31 10:36
 */
public class Contrast {

    //记录每个修改字段的分隔符
    public static final String separator = "|";

    /**
     * 对象和map进行对比
     *
     * @param pojo1
     * @param pojo2
     * @param cls
     * @return
     * @throws Exception
     */
    public static String contrastObj(Object pojo1, Map<String, String> pojo2, String key, Class cls) throws Exception {
        return contrastObj(pojo1, mapToObject(pojo2, cls), key);
    }

    /**
     * 比较两个对象,并返回不一致的信息
     */
    public static String contrastObj(Object pojo1, Object pojo2, String key) {
        ContrastResultBean resultBean = new ContrastResultBean();
        try {
            List<ContrastBean> contrastBeans = new ArrayList();
            Class clazz = pojo1.getClass();
            Field[] fields = pojo1.getClass().getDeclaredFields();
            int i = 1;
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();
                Object o1 = getMethod.invoke(pojo1);
                Object o2 = getMethod.invoke(pojo2);
                if (o2 == null) {
                    continue;
                }
                if (o1 != null && o1 instanceof Date) {
                    o1 = DateUtil.formatDate((Date) o1);
                }
                if (o1 != null && o2 instanceof Date) {
                    o2 = DateUtil.formatDate((Date) o2);
                }
                if (key != null && field.getName().equals(key)) {
                    resultBean.setId(o1.toString());
                }
                if (o1 == null && o2 != null) {
                    contrastBeans.add(new ContrastBean(field.getName(), "", o2));
                    i++;
                    continue;
                }
                if (!o1.toString().equals(o2.toString())) {
                    contrastBeans.add(new ContrastBean(field.getName(), o1, o2));
                    i++;
                }
            }
            resultBean.setList(contrastBeans);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.toJSONString(resultBean);
    }

    /**
     * map转成对象
     *
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToObject(Map<String, String> map, Class<?> beanClass) throws Exception {
        if (map == null)
            return null;
        Object obj = beanClass.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if (setter != null) {
                Class c = setter.getParameterTypes()[0];
                String parameterType = c.getTypeName();
                String value = map.get(property.getName());
                if (value != null && !"".equals(value)) {
                    Object v = value;
                    if ("int".equals(parameterType) || "java.lang.Integer".equals(parameterType)) {
                        v = Integer.parseInt(value);
                    } else if ("float".equals(parameterType) || "java.lang.Float".equals(parameterType)) {
                        v = Float.parseFloat(value);
                    } else if ("double".equals(parameterType) || "java.lang.Double".equals(parameterType)) {
                        v = Double.parseDouble(value);
                    } else if ("short".equals(parameterType) || "java.lang.Short".equals(parameterType)) {
                        v = Short.parseShort(value);
                    } else if ("long".equals(parameterType) || "java.lang.Long".equals(parameterType)) {
                        v = Long.parseLong(value);
                    } else if ("java.util.Date".equals(parameterType)) {
                        v = DateUtil.parseDate(value);
                    }
                    setter.invoke(obj, v);
                }
            }
        }
        return obj;
    }
}
