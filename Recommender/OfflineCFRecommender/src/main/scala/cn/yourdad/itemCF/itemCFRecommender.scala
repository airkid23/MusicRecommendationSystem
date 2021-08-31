package cn.yourdad.itemCF

import java.util

import cn.yourdad.mpping.{MappingID2RealID, RatingTransfrom}
import cn.yourdad.util.HBaseConnUtil
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, HTable, Put, ResultScanner, Scan, Table}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions._

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad
 * @description: 基于物品的协同过滤算法
 * @author: wzj
 * @create: 2020-10-15 13:44
 * */
object itemCFRecommender {


  def saveRecList2Hbase(recRDD: RDD[(String, String)]) = {

    recRDD.foreachPartition( line => {
      val config = HBaseConfiguration.create()
      config.addResource(
        new Path("/home/wzj/IdeaProjects/MusicRecommendationSystem/Recommender/DataLoader/src/main/resources/config/core-site.xml")
      )
      config.addResource(
        new Path("/home/wzj/IdeaProjects/MusicRecommendationSystem/Recommender/DataLoader/src/main/resources/config/hbase-site.xml")
      )
      val myTable = new HTable(config, TableName.valueOf("playlist"))
      line.foreach { a =>
        var put = new Put(Bytes.toBytes(a._1))
        put.addColumn(Bytes.toBytes("recs"), Bytes.toBytes("itemCFRec"), Bytes.toBytes(a._2))
        myTable.put(put)
      }
    })
  }

  def getSimPlaylist() ={

    val hbaseConnUtil = new HBaseConnUtil()
    val config = HBaseConfiguration.create()
    config.addResource(
      new Path("/home/wzj/IdeaProjects/MusicRecommendationSystem/Recommender/DataLoader/src/main/resources/config/core-site.xml")
    )
    config.addResource(
      new Path("/home/wzj/IdeaProjects/MusicRecommendationSystem/Recommender/DataLoader/src/main/resources/config/hbase-site.xml")
    )
    val conn: Connection = ConnectionFactory.createConnection(config)
    val table = conn.getTable(TableName.valueOf("playlist"))
    val scan = new Scan
    val rs: ResultScanner = table.getScanner(scan)
    val r = rs.iterator()
    val list = new util.ArrayList[(String, String)]()
    while (r.hasNext){
      val row = r.next()
      val rowkey = Bytes.toString(row.getRow)
      val simPL = Bytes.toString(row.getValue(Bytes.toBytes("recs"), Bytes.toBytes("lfSim")))
      if (simPL != null){
        list.add((rowkey, simPL))
      }
    }
    list
  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val sparkConf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[*]")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc = spark.sparkContext
    val idMapping = new MappingID2RealID

    val ratingTransfrom = new RatingTransfrom
    val ratingRDD = ratingTransfrom.getTransRatingRDD().cache()

    val SimPlRDD = sc.parallelize(getSimPlaylist())


    //获取用户评高分的歌单
    val userHighRatingRDD = ratingRDD.filter(_.rating >= 4.0)
      .groupBy(_.user)
      .mapValues(_.toList.map( line => (line.product, line.rating)))
      .map(line => {
        (idMapping.getRealUID(line._1), line._2.map(x => (idMapping.getRealPlaylistID(x._1), x._2)).mkString("|"))
      })
    //高分歌单与相似歌单做笛卡尔积
    val itemCFRec = userHighRatingRDD.cartesian(SimPlRDD)
      //过滤出歌单的相似歌单包含用户评高分的歌单
      .filter( line => {
        line._1._2.contains(line._2._1)
      })
      .map( x => ((x._1._1, x._1._2.split("\\|").toList), (x._2._1,x._2._2.split("\\|").toList.take(5))))
      .map( item => {
        (item._1, (item._2._1,item._2._2.map(x => x.split(",")(0).replace("(", ""))))
      })
      //得到推荐结果
      .map( item => {
        var str = ""
        item._1._2.map( x => {
          if(x.split(",")(0).replace("(", "") == item._2._1){
            str = x + "~" + item._2._2.mkString("|")
          }
        })
        (item._1._1, str)
      })
//itemCFRec.foreach(println)
    saveRecList2Hbase(itemCFRec)
  }
}
