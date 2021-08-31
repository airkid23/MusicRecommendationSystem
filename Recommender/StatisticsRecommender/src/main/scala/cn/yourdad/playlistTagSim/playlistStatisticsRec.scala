package cn.yourdad.playlistTagSim

import java.util.Date

import cn.yourdad.pojo.PlaylistInfoBean
import cn.yourdad.util.MysqlStoreUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.playlist
 * @description: 根据playlist 数据对歌单进行推荐
 * @author: wzj
 * @create: 2020-09-27 12:49
 * */


object playlistStatisticsRec {


  val conf = new SparkConf().setMaster("local[*]").setAppName("playlistStatisticsRec")
  val spark = SparkSession.builder().config(conf).getOrCreate()
  import spark.implicits._
  val mysqlStoreUtil = new MysqlStoreUtil

  def main(args: Array[String]): Unit = {

    val playlistDF = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "playlist")
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .load()
      .as[PlaylistInfoBean]
      .toDF()
      .cache()

    playlistDF.registerTempTable("playlist")
    //由于在mysql里字段都是string， 需要写udf将string转换为Long
    spark.udf.register("stringToLong", (count: String) => count.toLong)


    val plMostPlayDF = spark.sql(" select playlistID from playlist order by stringToLong(playCount) desc limit 50")
    mysqlStoreUtil.save2Mysql(plMostPlayDF, "plMostPlay")

    println("===========================================================================================================")

    val plMostShareDF = spark.sql("select playlistID from playlist order by stringToLong(shareCounts) desc limit 50")
    mysqlStoreUtil.save2Mysql(plMostShareDF, "plMostShare")

    println("===========================================================================================================")

    val plMostCollectDF = spark.sql("select playlistID from playlist order by stringToLong(collectCount) desc limit 50")
    mysqlStoreUtil.save2Mysql(plMostCollectDF, "plMostCollect")

    println("===========================================================================================================")

    val plMostCommentsDF = spark.sql("select playlistID from playlist order by stringToLong(commentsCount) desc limit 50")
    mysqlStoreUtil.save2Mysql(plMostCommentsDF, "plMostComments")
  }
}
