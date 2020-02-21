package com.yy.service.impl;

import com.yy.dto.Exposer;
import com.yy.dto.SeckillExecution;
import com.yy.entity.Seckill;
import com.yy.exception.RepeatKillException;
import com.yy.exception.SeckillCloseException;
import com.yy.service.SeckillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list=seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    /**
     * exposer=Exposer{exposed=true, md5='ff10784e00d19735e41a96eecf5e352a', seckillId=1000, now=0, start=0, end=0}
     * com.yy.service.impl.SeckillServiceImplTest - seckill repeated
     */
    public void testSeckillLogic() throws Exception {
        long id = 1000L;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);

            long phone=12345678903L;
            String md5=exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("execution={}", execution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else{
            logger.info("exposer={}",exposer);
        }

    }

}