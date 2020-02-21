package com.yy.dao;

import com.yy.entity.Seckill;
import com.yy.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合，这样junit启动时加载SpringIOC容器
 * spring-test，junit这两个包帮我们做以下事情
 */
//使junit启动时加载SpringIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件在哪
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() throws Exception {
        long id=1000;
        Seckill seckill=seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill.toString());
    }

    @Test
    public void queryAll() throws Exception {
        /**
         * 运行这个方法时会报这个错误：
         * BindingException: Parameter 'offset' not found
         * 问题出在sql映射文件上，sql映射文件找不到offset这个参数。原因是反射方法时并不会保存形参的名字，只会用arg0..n表示参数。
         * 解决办法，在Dao接口的每个方法的参数前面增加注解，如@Param("offset")以保留形参名。
         */
        List<Seckill> list=seckillDao.queryAll(0,100);
        for(Seckill kill:list)
            System.out.println(kill.toString());
    }

    @Test
    public void reduceNumber() throws Exception {
        Date killTime=new Date();
        int count=seckillDao.reduceNumber(1000,killTime);
        System.out.println("count:"+count);
    }



}