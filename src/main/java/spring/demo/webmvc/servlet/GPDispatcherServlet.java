package spring.demo.webmvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import spring.demo.annotation.GPController;
import spring.demo.annotation.GPRequestMapping;
import spring.demo.context.GPApplicationContext;
import spring.demo.webmvc.GPHandlerAdapter;
import spring.demo.webmvc.GPHandlerMapping;
import spring.demo.webmvc.GPModelAndView;
import spring.demo.webmvc.GPView;
import spring.demo.webmvc.GPViewResolver;

/**
 * Servlet implementation class GPDispatcherServlet
 */

// servlet 只是作为一个mvc的启动入口
@Slf4j
public class GPDispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final String LOCATION = "contextConfigLocation";
	
	// 读者可以思考一下这样设计的经典之处
	// GPHandlerMapping 最核心的设计,也是最经典的
	// 它直接干掉了Structs、Webwork等mvc框架
	private List<GPHandlerMapping> handlerMappings = new ArrayList<GPHandlerMapping>();
	
	private Map<GPHandlerMapping,GPHandlerAdapter> handlerAdapters = new HashMap<GPHandlerMapping,GPHandlerAdapter>();
	
	private List<GPViewResolver> viewResolvers = new ArrayList<GPViewResolver>();
	
	private GPApplicationContext context;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GPDispatcherServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        // 相当于把IOC容器初始化
        context = new GPApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }
    
	private void initStrategies(GPApplicationContext context2) {
        // TODO Auto-generated method stub
        // 有九种策略
	    // 针对每个用户请求,都会经过一些处理策略处理，最终才能有结果输出
	    // 每种策略可以自定义干预，但是最终的结果都一致
	    // ======================= 这里就是传说中的九大组件 =============
	    initMultipartResolver(context); // 文件上传解析，如果请求类型是multipart,将通过MultipartResolver进行文件解析
	    initLocaleResolver(context); // 本地化解析
	    initThemeResolver(context); // 主题解析
	    
	    /** 我们自己会实现*/
	    // GPHandlerMapping 用来保存Controller中配置的RequestMapping 和 Method的对应关系
	    initHandlerMappings(context); //通过handlerMapping将请求映射到处理器
	    /** 我们自己会实现*/
	    // HandlerAdapters 用来动态匹配Method参数， 包括类型转换、动态赋值
	    initHandlerAdapters(context); // 通过HandlerAdapter 进行多类型的参数动态匹配
	    
	    initHandlerExceptionResolvers(context); // 如果执行过程中遇到异常，将交给HandlerExceptionResolver来解析
	    initRequestToViewNameTranslator(context); // 直接将请求解析到视图名
	    
	    /** 我们自己回实现 */
	    // 通过ViewResolvers 实现动态模板的解析
	    // 自己解析一套模板语言
	    initViewResolvers(context); // 通过viewResolver 将逻辑视图解析到具体视图实现
	    
	    initFlashMapManager(context); //Flash映射管理器
    }


    

    private void initFlashMapManager(GPApplicationContext context2) {}
    private void initRequestToViewNameTranslator(GPApplicationContext context2) {}
    private void initHandlerExceptionResolvers(GPApplicationContext context2) {}
    private void initThemeResolver(GPApplicationContext context2) {}
    private void initLocaleResolver(GPApplicationContext context2) {}
    private void initMultipartResolver(GPApplicationContext context2) {}
    
    // 将Controller 中配置的RequestMapping 和method意义对应
    private void initHandlerMappings(GPApplicationContext context) {
        // TODO Auto-generated method stub
        // 按照我们通常的理解应该是一个Map
        // Map<String,Method>
        //map.put(url,method);
        
        // 首先从容器中获取所有的实例
        String [] beanNames = context.getBeanDefinitionNames();
        
        try {
            for(String beanName : beanNames) {
                // 到了MVC层，对外提供的方法只有一个getBean();
                // 返回的对象不是BeanWrapper,怎么办？
                Object controller = context.getBean(beanName);
                if(controller == null) {
                    continue;
                }
                //Object controller = GPAopUtils.getTargetObject(proxy);
                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(GPController.class)) {
                    continue;
                }
                
                String baseUrl = "";
                if(clazz.isAnnotationPresent(GPRequestMapping.class)) {
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                
                // 扫描所有的puvlic类型的方法
                Method[] methods = clazz.getMethods();
                for(Method method : methods) {
                    if(!method.isAnnotationPresent(GPRequestMapping.class)) {
                        continue;
                    }
                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                    String regex = ("/" + baseUrl + requestMapping.value()).replaceAll("\\*", ".*").replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new GPHandlerMapping(controller, method, pattern));
                    log.info("Mapping: " + regex + "," + method);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void initHandlerAdapters(GPApplicationContext context) {
        // TODO Auto-generated method stub
        // 在初始化阶段,我们能做的就是,将这些参数的名字或者类型按一定的顺序保存下来
        // 因为后面用反射调用的时候，传的形参是一个数组
        // 可以通过记录这些参数的位置index,逐个从数组中取值，这样就和参数的顺序无关了
        for(GPHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new GPHandlerAdapter());
        }
    }
    
    private void initViewResolvers(GPApplicationContext context) {
        // TODO Auto-generated method stub
        // 在页面输入http://localhost/first.html
        // 解决页面名字和模板文件关联问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = GPDispatcherServlet.class.getClassLoader().getResource(templateRoot).getFile();
        
        File templateRootDir = new File(templateRootPath);
        
        for(File template : templateRootDir.listFiles()) {
           // this.viewResolvers.add(new GPViewResolver(templateRootDir.getAbsolutePath() + "\\" +template.getName(),template.getName()));//?
            this.viewResolvers.add(new GPViewResolver(templateRoot,template.getName()));//?
        }
    }
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    try {
            doDispatch(req,resp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resp.getWriter().write("五百");
        }
	}

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // TODO Auto-generated method stub
        // 根据用户请求的URL来获得一个handler
        GPHandlerMapping handler = getHandler(req);
        if(handler == null) {
            processDispatchResult(req,resp,new GPModelAndView("404"));
            return ;
        }
        
        GPHandlerAdapter ha = getHandlerAdapter(handler);
        
        // 这一步只是调用方法，得到返回值
        GPModelAndView mv = ha.handle(req, resp, handler);
        
        // 这一步才是真的输出
        processDispatchResult(req,resp,mv);
    }

    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping handler) {
        // TODO Auto-generated method stub
        if(this.handlerAdapters.isEmpty()) {
            return null;
        }
        
        GPHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)) {
            return ha;
        }
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp,
            GPModelAndView mv) throws Exception {
        // TODO Auto-generated method stub
        // 调用viewResolver的resolveViewName()方法
        if(null == mv) {
            return ;
        }
        
        if(this.viewResolvers.isEmpty()) {
            return ;
        }
        
        if(this.viewResolvers != null) {
            for(GPViewResolver viewResolver : this.viewResolvers) {
                GPView view = viewResolver.resolveViewName(mv.getViewName(), null);
                if(view != null) {
                    view.render(mv.getModel(), req, resp);
                    return;
                }
            }
        }
    }

    private GPHandlerMapping getHandler(HttpServletRequest req) {
        // TODO Auto-generated method stub
        if(this.handlerMappings.isEmpty()) {
            return null;
        }
        
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        
        for(GPHandlerMapping handler : handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()) {
                continue;
            }
            
            return handler;
        }
        
        return null;
    }
    public static void main(String[] args) {
        System.out.println(GPDispatcherServlet.class.getClassLoader().getResource("").getFile());
    }
}
