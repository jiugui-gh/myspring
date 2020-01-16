package spring.demo.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

// 用老保存URL和Method对应关系，这里其实是使用策略模式
public class GPHandlerMapping {

    private Object controller; //目标方法所在的controller对象
    private Method method; // URL对应的目标方法
    private Pattern pattern; //URL的封装
    public GPHandlerMapping(Object controller, Method method, Pattern pattern) {
        super();
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
    public Object getController() {
        return controller;
    }
    public void setController(Object controller) {
        this.controller = controller;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }
    public Pattern getPattern() {
        return pattern;
    }
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
    
    
}
