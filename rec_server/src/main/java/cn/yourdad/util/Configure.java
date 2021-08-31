package cn.yourdad.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description:
 * @author: wzj
 * @create: 2020-10-14 21:48
 **/

@Configuration
public class Configure {

    @Value("${spring.redis.host}")
    private String redisHost;

    public Configure(){

    }


    @Bean(name = "jedis")
    public Jedis getRedisClient() {

        Jedis jedis = new Jedis(this.redisHost);
        return jedis;
    }
}
