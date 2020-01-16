package spring.demo.beans.config;

// 只要用于保存bean相关的配置信息
// 相当于用来存储配置文件中的信息
// 相当于保存在内存中的配置
public class GPBeanDefinition {

    private String beanClassName; // 原生bean的全类名
    private boolean lazyInit = false; // 标记是否延迟加载
    private String factoryBeanName; // 保存beanName,在ioc容器中存储的key
    
    public String getBeanClassName() {
        return beanClassName;
    }
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
    public boolean isLazyInit() {
        return lazyInit;
    }
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    public String getFactoryBeanName() {
        return factoryBeanName;
    }
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
    
    
}
