package cn.yourdad.util

import redis.clients.jedis.Jedis

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description: ${description}
 * @author: wzj
 * @create: 2020-10-17 23:03
 * */
class JedisUtil extends Serializable {

  def apply(): JedisUtil = new JedisUtil()

  def getJedis():Jedis = {
    val jedis = new Jedis("localhost")
    jedis
  }
}
