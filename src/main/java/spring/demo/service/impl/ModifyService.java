package spring.demo.service.impl;

import spring.demo.annotation.GPService;
import spring.demo.service.IModifyService;

@GPService
public class ModifyService implements IModifyService {

    @Override
    public String add(String name, String addr) {
        // TODO Auto-generated method stub
        return "modifyService add,name=" + name + ",addr=" + addr;
    }

    @Override
    public String edit(Integer id, String name) {
        // TODO Auto-generated method stub
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    @Override
    public String remove(Integer id) {
        // TODO Auto-generated method stub
        return "modifyService id=" + id;
    }

}
