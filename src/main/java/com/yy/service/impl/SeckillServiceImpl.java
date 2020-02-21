package com.yy.service.impl;

import com.yy.dao.SeckillDao;
import com.yy.dao.SuccessKilledDao;
import com.yy.dao.cache.RedisDao;
import com.yy.dto.Exposer;
import com.yy.dto.SeckillExecution;
import com.yy.entity.Seckill;
import com.yy.entity.SuccessKilled;
import com.yy.enums.SeckillStateEnum;
import com.yy.exception.RepeatKillException;
import com.yy.exception.SeckillCloseException;
import com.yy.exception.SeckillException;
import com.yy.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //md5盐值字符串，用于混淆MD5
    private final String salt="skdfjksjdf7787%^%^%^FSKJFK*(&&%^%&^8DF8^%^^*7hFJDHFJ";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    private String getMd5(long seckillId){
        String base=seckillId+"/"+salt;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化
//        Seckill seckill=seckillDao.queryById(seckillId);
        //1、“从数据库中取出Seckill”换成“访问redis以取出Seckill”
        Seckill seckill=redisDao.getSeckill(seckillId);
        if(seckill==null){
//            return new Exposer(false,seckillId);
            //以下为新加
            //2、如果redis未缓存，就去访问数据库
            seckill=seckillDao.queryById(seckillId);
            if(seckill==null)
                return new Exposer(false,seckillId);
            else
                //3、访问redis
                redisDao.putSeckill(seckill);
        }
        long startTime=seckill.getStartTime().getTime();
        long endTime=seckill.getEndTime().getTime();
        long nowTime=new Date().getTime();
        if(nowTime<startTime||nowTime>endTime)
            return new Exposer(false,seckillId,nowTime,startTime,endTime);
        String md5=getMd5(seckillId);
        //必须要拿到md5才能执行秒杀操作
        return new Exposer(true,md5,seckillId);
    }

    @Override
    @Transactional
    /**
     * 与AOP控制事务方法相比，使用注解控制事务方法的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException,SeckillCloseException,SeckillCloseException {
        if(md5==null||!md5.equals(getMd5(seckillId)))
            throw new SeckillException("seckill data rewrite");
        //执行秒杀逻辑：减库存+记录购买行为
        Date now=new Date();

        try {
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0)
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            else {
                //减库存，热点商品竞争
                //由于update操作需要拿到mysql的行级锁，为了减少行级锁占有时间，这块的逻辑是先insert再update
                int updateCount = seckillDao.reduceNumber(seckillId, now);
                if (updateCount <= 0)
                    //没有更新到记录，rollback
                    throw new SeckillCloseException("seckill is closed");
                else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }catch (SeckillCloseException e1) {
            throw e1;
        }catch (RepeatKillException e2) {
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage());
            //将所有受查异常转换为RuntimeException
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if(md5==null||!md5.equals(getMd5(seckillId)))
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        Date killTime=new Date();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            int result= MapUtils.getInteger(map,"result",-2);
            if(result==1){
                SuccessKilled sk=successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,sk);
            }else{
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }
    }
}
