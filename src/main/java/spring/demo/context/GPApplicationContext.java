package spring.demo.context;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import spring.demo.annotation.GPAutowired;
import spring.demo.annotation.GPController;
import spring.demo.annotation.GPService;
import spring.demo.beans.GPBeanWrapper;
import spring.demo.beans.config.GPBeanDefinition;
import spring.demo.beans.config.GPBeanPostProcessor;
import spring.demo.beans.support.GPBeanDefinitionReader;
import spring.demo.beans.support.GPDefaultListableBeanFactory;
import spring.demo.core.GPBeanFactory;

/**
 * 主要实现GPDefaultListableBeanFactory的refresh()  和getBean 方法 
 * 完成IOC DI AOP的衔接
 * @author Pinkboy
 *
 */
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    private String []configLocations;
    private GPBeanDefinitionReader reader;
    
    
    // 单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String,Object>();
    // 通用的Ioc容器
    private Map<String,GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String,GPBeanWrapper>();
    
     public GPApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        
        try {
            refresh();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public void refresh() throws Exception {
        // TODO Auto-generated method stub
        // 1.定位,定位配置文件
        reader = new GPBeanDefinitionReader(configLocations);
        
        // 2.加载配置文件,扫描相关的类，把他们封装成BeanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        
        // 3.注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);
        
        // 4.把不是延迟加载的类提前初始化 
        doAutowrited();
    }
    
    // 只处理非延时加载的情况
    private void doAutowrited() {
        // TODO Auto-generated method stub
        for(Entry<String,GPBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) throws Exception {
        // TODO Auto-generated method stub
        for(GPBeanDefinition beanDefinition : beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exists");
            }
            
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        // 到这里容器初始化完笔
    }

    // 依赖注入，从这里开始 ，读取BeanDefinition中的信息
    // 然后通过反射机制创建一个实例返回
    // Spring的做法是，不会把最原始的对象放进去，回用一个BeanWrapper来进行一次包装
    // 装饰者模式
    // 1.保留原来的OOP关系
    // 2.需要对他进行扩展，增强（为了以后的AOP打基础）
    @Override
    public Object getBean(String beanName) throws Exception {
        // TODO Auto-generated method stub
        GPBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        
        try {
            //生成通知时间
            GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();
            
            Object instance = instantiateBean(beanDefinition);
            if(null == instance) {
                return null;
            }
            
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            
            GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);
            
            this.factoryBeanInstanceCache.put(beanName, beanWrapper);
            
            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            
            populateBean(beanName,instance);
            
            // 通过这样调用，相当于给我们自己留了可操作的空间
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    private void populateBean(String beanName, Object instance) {
        // TODO Auto-generated method stub
        Class<?> clazz = instance.getClass();
        
        if(!(clazz.isAnnotationPresent(GPController.class) ||
                clazz.isAnnotationPresent(GPService.class))) {
            return ;
        }
        
        Field[] fields = clazz.getDeclaredFields();
        
        for(Field field : fields) {
            if(!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }
            
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            
            if("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            
            field.setAccessible(true);
            
            try {
                field.set(instance, this.factoryBeanObjectCache.get(autowiredBeanName));
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(GPBeanDefinition beanDefinition) {
        // TODO Auto-generated method stub
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        
        try {
            //因为根据Class才能确定一个类是否有实例
            if(this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                
                if(!(clazz.isAnnotationPresent(GPService.class) || clazz.isAnnotationPresent(GPController.class))) {
                    return null;
                }
                instance = clazz.newInstance();
                //this.factoryBeanObjectCache.put(beanDefinition.getBeanClassName(), instance);
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
            
            return instance;
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        // TODO Auto-generated method stub
        
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }
    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
