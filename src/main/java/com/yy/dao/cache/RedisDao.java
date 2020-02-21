package com.yy.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.yy.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    private final JedisPool jedisPool;
    private RuntimeSchema<Seckill> schema=RuntimeSchema.createFrom(Seckill.class);
    public RedisDao(String ip,int port){
        jedisPool=new JedisPool(ip,port);

    }

    public Seckill getSeckill(long seckillId){
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                //由于并没有实现内部序列化操作，所以采用自定义序列化
                //get->byte[]->反序列化->object
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    //先创建一个空对象
                    Seckill seckill = schema.newMessage();
                    //将反序列化后的内容填入空对象中
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    return seckill;
                }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill){
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                int timeout = 60 * 60;
                //超时缓存，是最简单的缓存维护方式
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
