package spring.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import spring.demo.annotation.GPService;
import spring.demo.service.IQueryService;

@GPService
@Slf4j
public class QueryService implements IQueryService {

    @Override
    public String query(String name) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
        log.info("这是在业务方法中打印的：" + json);
        return json;
    }

}
