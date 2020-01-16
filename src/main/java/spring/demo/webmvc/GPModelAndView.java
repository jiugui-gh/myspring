package spring.demo.webmvc;

import java.util.Map;
// 用于封装页面模板和往页面传送参数的对应关系
public class GPModelAndView {

    private String viewName; // 页面模板的名称
    private Map<String,?> model; // 往页面传送的参数
    
    
    public GPModelAndView(String viewName) {
        this(viewName,null);
    }


    public GPModelAndView(String viewName, Map<String, ?> model) {
        super();
        this.viewName = viewName;
        this.model = model;
    }


    public String getViewName() {
        return viewName;
    }


    public void setViewName(String viewName) {
        this.viewName = viewName;
    }


    public Map<String, ?> getModel() {
        return model;
    }


    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
    
    
}
