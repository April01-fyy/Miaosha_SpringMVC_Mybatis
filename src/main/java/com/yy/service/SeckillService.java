package com.yy.service;

import com.yy.dto.Exposer;
import com.yy.dto.SeckillExecution;
import com.yy.entity.Seckill;
import com.yy.exception.SeckillCloseException;
import com.yy.exception.SeckillException;

import java.util.List;

/**
 * 业务接口的设计必须要站在使用者的角度。从三个方面去考虑：
 * 方法定义粒度、参数、返回类型（包括异常）。
 */
public interface SeckillService {
    /**
     * 查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @throws Exception
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException,SeckillCloseException,SeckillCloseException;


    /**
     * 用存储过程执行秒杀操作
     * 这里不需要异常了，因为上面那个函数我们是打算通过抛出异常使声明式事务回滚，
     * 而现在用了存储过程，在服务器端就已经处理好何时回滚了
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);

}
