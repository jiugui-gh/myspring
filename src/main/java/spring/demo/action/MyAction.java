package spring.demo.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spring.demo.annotation.GPAutowired;
import spring.demo.annotation.GPController;
import spring.demo.annotation.GPRequestMapping;
import spring.demo.annotation.GPRequestParam;
import spring.demo.service.IModifyService;
import spring.demo.service.IQueryService;
import spring.demo.webmvc.GPModelAndView;

@GPRequestMapping("/web")
@GPController
public class MyAction {

    @GPAutowired
    private IQueryService queryService;
    
    @GPAutowired
    private IModifyService modifyService;
    
    @GPRequestMapping("/query.json")
    public GPModelAndView query(HttpServletRequest request,HttpServletResponse response,@GPRequestParam("name") String name) {
        String result = queryService.query(name);
        return out(response,result);
    }
    
    @GPRequestMapping("/add*.json")
    public GPModelAndView add(HttpServletRequest request,HttpServletResponse response,@GPRequestParam("name") String name,@GPRequestParam("addr") String addr) {
        String result = modifyService.add(name, addr);
        return out(response,result);
    }
    
    @GPRequestMapping("/remove.json")
    public GPModelAndView remove(HttpServletRequest request,HttpServletResponse response,@GPRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        return out(response,result);
    }

    @GPRequestMapping("/edit.json")
    public GPModelAndView edit(HttpServletRequest request,HttpServletResponse response,@GPRequestParam("id") Integer id,@GPRequestParam("name") String name) {
        String result = modifyService.edit(id,name);
        return out(response,result);
    }
    
    private GPModelAndView out(HttpServletResponse response, String result) {
        // TODO Auto-generated method stub
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
