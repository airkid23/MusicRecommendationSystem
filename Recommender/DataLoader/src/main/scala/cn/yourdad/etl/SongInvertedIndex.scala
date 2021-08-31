package cn.yourdad.etl

import java.util

import cn.yourdad.const.Const
import cn.yourdad.pojo.SongInfoBean
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{HTable, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import scala.collection.mutable

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.etl
 * @description: 歌曲倒排索引
 * @author: wzj
 * @create: 2020-10-10 22:31
 * */
case class plID_songIDs(playlistID: String, songsID:String)

object SongInvertedIndex {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._
    val sc = spark.sparkContext
    val const = new Const


//    val songIDRDD = spark.read
//      .format("jdbc")
//      .option("driver", "com.mysql.jdbc.Driver")
//      .option("dbtable", "songs")
//      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
//      .option("user","root")
//      .option("password","root")
//      .load()
//      .as[SongInfoBean]
//      .rdd
//      .map(_.songID)
//      .distinct()


    val plID_songIDS_RDD = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "songsID")
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .load()
      .as[Playlist_SongsID]
      .rdd

    val SongInvertedIndexRDD = plID_songIDS_RDD.map(line => {
      (line.playlistID, line.songsID.split(",").flatMap(line => line.split(" ")))
    })
      .flatMap( x => x._2.distinct.map(word=>(word,x._1)))
      .reduceByKey(_ + "|"+ _)



    SongInvertedIndexRDD.foreachPartition( line => {
      val config = HBaseConfiguration.create()
      config.addResource(
        new Path(ClassLoader.getSystemResource("config/core-site.xml").toURI)
      )
      config.addResource(
        new Path(ClassLoader.getSystemResource("config/hbase-site.xml").toURI)
      )

      val myTable = new HTable(config, TableName.valueOf("playlist"))
      line.foreach { a =>
        var put = new Put(Bytes.toBytes(a._1))
        put.addColumn(Bytes.toBytes("song"), Bytes.toBytes("InvertedIndex"), Bytes.toBytes(a._2))
        myTable.put(put)
      }
    })




  }
}
