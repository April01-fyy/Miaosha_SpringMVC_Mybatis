package com.yy.dao;

import com.yy.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

//使junit启动时加载SpringIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件在哪
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        long id=1000L;
        long phone=12345678901L;
        int count=successKilledDao.insertSuccessKilled(id,phone);
        System.out.println("insertCount:"+count);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long id=1000L;
        long phone=12345678901L;
        SuccessKilled successKilled=successKilledDao.queryByIdWithSeckill(id,phone);
        System.out.println(successKilled.toString());
        System.out.println(successKilled.getSeckill().toString());
    }

}