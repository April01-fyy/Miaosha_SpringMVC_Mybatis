package com.yy.dao;

import com.yy.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 2、写好Dao
 * DAO写好后，接下来我们就要用mybatis实现这些DAO！
 * mybatis和hibernate的区别是，mybatis的sql需要我们去写，因此mybatis提供了很大的灵活性，这时我们可以充分发挥sql的技巧。
 * 使用mybatis只需要提供参数和sql即可，mybatis（和hibernate）底层会通过jdbc方式将查询结果封装成实体或者实体列表返回给我们使用。
 */
public interface SeckillDao {
    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     * @param seckillId
     * @return
     */
    Seckill queryById(@Param("seckillId") long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);


    /**
     * 使用存储过程执行秒杀
     *
     * @param paramMap
     */
    void killByProcedure(Map<String, Object> paramMap);

}
