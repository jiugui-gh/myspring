package spring.demo.beans.config;

public class GPBeanPostProcessor {

    // 为在bean的初始化之前提供回调入口
    public Object postProcessBeforeInitialization(Object bean,String beanName) throws Exception {
        return bean;
    } 
    
    // 为在Bean的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean,String beanName) throws Exception {
        return bean;
    }
}
