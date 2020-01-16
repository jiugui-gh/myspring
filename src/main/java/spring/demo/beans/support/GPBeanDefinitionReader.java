package spring.demo.beans.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import spring.demo.beans.config.GPBeanDefinition;

/**
 * 主要完成application.properties配置文件的解析工作
 * 通过构造方法获取从ApplicationContext传过来的locations配置文件路径，然后解析
 * 扫描并保存所有相关的类并提供统一的访问入口
 * @author Pinkboy
 *
 */

// 对配置文件进行查找、读取、解析
public class GPBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();
    private Properties config = new Properties();
    
    // 固定配置文件的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";
    
    public GPBeanDefinitionReader(String... locations) {
        // 通过URL定位找到其锁对应的文件,然后转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        // TODO Auto-generated method stub
        File classPath = new File(this.getClass().getClassLoader().getResource("/" + scanPackage.replace("\\.", "/")).getFile());
        
        for(File file : classPath.listFiles()) {
            if(file.isDirectory()) {
                doScanner(scanPackage + "." +file.getName());
            }else {
                if(!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class", "");
                registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return config;
    }
    
    // 把配置文件中扫描到的所有配置信息转换为GPBeanDefinition 对象,以便于之后的IOC操作
    public List<GPBeanDefinition> loadBeanDefinitions() {
        List<GPBeanDefinition> result = new ArrayList<GPBeanDefinition>();
        
        try {
            for(String className : registyBeanClasses) {
                Class<?> clazz = Class.forName(className);
                if(clazz.isInterface()) {
                    continue;
                }
                result.add(doCreaetBeanDefinition(toLowerFirstCase(clazz.getSimpleName()),clazz.getName()));
                
                Class<?>[] interfaces = clazz.getInterfaces();
                for(Class<?> i : interfaces) {
                    result.add(doCreaetBeanDefinition(i.getName(),clazz.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
        
    }

    private GPBeanDefinition doCreaetBeanDefinition(String factoryBeanName, String beanClassName) {
        // TODO Auto-generated method stub
        GPBeanDefinition beanDefinition = new GPBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        // TODO Auto-generated method stub
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
