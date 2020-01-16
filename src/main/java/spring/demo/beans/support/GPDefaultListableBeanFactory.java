package spring.demo.beans.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import spring.demo.beans.config.GPBeanDefinition;
import spring.demo.context.support.GPAbstractApplicationContext;
/**
 * 定义顶层ioc缓存，也就是一个map，属性名也和原生spring保持一致
 * @author Pinkboy
 *
 */
public class GPDefaultListableBeanFactory extends GPAbstractApplicationContext {

    // 存储注册信息的BeanFefinition
    protected final Map<String,GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,GPBeanDefinition>();
}
