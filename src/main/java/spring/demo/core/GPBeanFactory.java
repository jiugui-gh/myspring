package spring.demo.core;
// 作为ioc容器的顶层设计   单例工厂的顶层设计
public interface GPBeanFactory {

    /**
     *   根据beanName从IOC容器中获取一个bean实例
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;
    
    public Object getBean(Class<?> beanClass) throws Exception;
}
