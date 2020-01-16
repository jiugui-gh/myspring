package spring.demo.webmvc;

import java.io.File;
import java.util.Locale;

/**
 * 设计这个类的主要目的是
 * 1.将一个静态文件变成动态文件
 * 2.根据用户传送不同的参数，产生不同的结果
 * 最终输出字符串，交给response输出
 * @author Pinkboy
 *
 */
public class GPViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    private File templateRootDir;
    private String viewName;
    
    public GPViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }
    
    public GPView resolveViewName(String viewName,Locale locale) throws Exception{
        
        this.viewName = viewName;
        if(null == this.viewName || "".equals(this.viewName.trim())) {
            return null;
        }
        this.viewName = this.viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? this.viewName : (this.viewName + DEFAULT_TEMPLATE_SUFFIX);
        
        File templateFile = new File((templateRootDir.getPath() + "/" + this.viewName).replaceAll("/+", "."));
        
        return new GPView(templateFile);
    }
    
   /* public String getViewName() {
        return viewName;
    }*/
     
}
