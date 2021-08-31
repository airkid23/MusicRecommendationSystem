package cn.yourdad.onlineItemCFRecommender

import java.util
import java.util.concurrent.TimeUnit
import java.util.{Collections, List, Properties}

import cn.yourdad.pojo.Rating
import cn.yourdad.util.JedisUtil
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, createTypeInformation}
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.async.{ResultFuture, RichAsyncFunction}
import org.apache.flink.streaming.api.scala.async.AsyncFunction
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.scala.{AsyncDataStream, StreamExecutionEnvironment, async}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.util.Collector
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}
import org.apache.flink.types.{Nothing, Row}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.userBehaviorRecommender
 * @description: 得到用户评分，实时进行itemcf推荐
 * @author: wzj
 * @create: 2020-10-16 15:40
 * */

object ConnHelper extends Serializable{
  lazy val jedis = new Jedis("localhost")
}
case class playlistTagSimBean(playlistID: String, tagSim:String)
object itemCFRecommender {

  def jdbcRead(batchEnv: ExecutionEnvironment): DataSet[Row] = {
    val inputMysql: DataSet[Row] = batchEnv.createInput(JDBCInputFormat.buildJDBCInputFormat()
      .setDrivername("com.mysql.jdbc.Driver")
      .setDBUrl("jdbc:mysql://localhost:3306/musicRecs?useSSL=false")
      .setUsername("root")
      .setPassword("root")
      .setQuery("select playlistID, tagSim from playlistTagSim")
      .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO))
      .finish()

    )
    inputMysql
  }


  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)

    val batchEnv = ExecutionEnvironment.getExecutionEnvironment
    val tableEnv = TableEnvironment.getTableEnvironment(batchEnv)
    val playlistTagSim= jdbcRead(batchEnv)
//      .withBroadcastSet(playlistTagSim, "playlistTagSim")
    import org.apache.flink.table.api.scala._

    tableEnv.registerDataSet("playlistTagSim", playlistTagSim, 'playlistID, 'tagSim)
    val result = tableEnv.sqlQuery("select *  from playlistTagSim where playlistID = '5014508678'")
    val rows = result.collect().mkString("")
    print(rows)


    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "log-consumer")
    properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("auto.offset.reset", "latest")

    val dataStream = env.addSource( new FlinkKafkaConsumer[String]("requestLog", new SimpleStringSchema(), properties))

    val ratingStream = dataStream.filter( _.contains("insertPlaylistScore2Rating"))
      .map(line => {
        val arrays = line.split("\t")
        arrays(4).stripPrefix("args:[").stripSuffix("]")
      })
      .map( item => {
        val arrays =  item.split(",")
        Rating(arrays(0).trim, arrays(1).trim, arrays(2).trim.toDouble)
      })

//      .process( new GetItemCFRecommendResult() )

    val resultStream=AsyncDataStream.unorderedWait(ratingStream,new RedisAsyncFunction(), 1000, TimeUnit.MILLISECONDS, 100)
    resultStream.print()
    env.execute(this.getClass.getSimpleName)
  }

}

class RedisAsyncFunction extends AsyncFunction[Rating,String]{
  lazy  val pool = new JedisPool(new JedisPoolConfig,"localhost",6379)
  override def asyncInvoke(input: Rating, resultFuture: async.ResultFuture[String]): Unit = {
    println("rating data is coming!!!")
    println(input)
    Future {
      //获取歌单id
      val playlistID = input.playlistID
      //从redis中获取imei对应的userid
      val jedis = pool.getResource
      var list: util.List[String] = new util.ArrayList[String]()
      list = jedis.lrange("tagsim:" + playlistID, 0, 5)
//      for (i <- 0 until list.size) {
//        println("列表项为: " + list.get(i))
//      }

      resultFuture.complete(list.asScala)
      pool.returnResource(jedis)
    }(ExecutionContext.global)
  }

  def getRecentPlayEd(uid:String) = {

  }
}

class GetItemCFRecommendResult() extends ProcessFunction[Rating, Unit]{

  private val jedisUtil = new JedisUtil()
  private val jedis = jedisUtil.getJedis

  override def processElement(value: Rating, ctx: ProcessFunction[Rating, Unit]#Context, out: Collector[Unit]): Unit = {
    val list: util.List[String] = jedis.lrange("tagsim:" + value.playlistID, 0, 5)
    println(value.playlistID)
    for (i <- 0 until list.size) {
      System.out.println( list.get(i))
    }
  }
}
