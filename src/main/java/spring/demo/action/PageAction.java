package spring.demo.action;

import java.util.HashMap;

import spring.demo.annotation.GPAutowired;
import spring.demo.annotation.GPController;
import spring.demo.annotation.GPRequestMapping;
import spring.demo.annotation.GPRequestParam;
import spring.demo.service.IQueryService;
import spring.demo.webmvc.GPModelAndView;

@GPController
@GPRequestMapping("/")
public class PageAction {

    @GPAutowired
    IQueryService queryService;
    
    @GPRequestMapping("/first.html")
    public GPModelAndView query(@GPRequestParam("teacher")String teacher) {
        String result = queryService.query(teacher);
        HashMap<String, Object> model = new HashMap<String,Object>();
        model.put("teacher",teacher);
        model.put("data", result);
        model.put("token", "123456");
        
        return new GPModelAndView("first.html",model);
    }
}
