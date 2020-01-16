package spring.demo.beans;

/**
 * 主要用于封装创建后的对象实例，代理对象或者原生对象  都有beanWrapper来保存
 * @author Pinkboy
 *
 */
public class GPBeanWrapper {
    
    private Object wrappedInstance;
   // private Class<?> wrappedClass;
    
    public GPBeanWrapper(Object wrappedInstance) {
        super();
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }
    
    // 返回代理以后的Class
    // 可能会是这个$proxy0
    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }

  /*  public void setWrappedClass(Class<?> wrappedClass) {
        this.wrappedClass = wrappedClass;
    }*/

   
    
    
}
