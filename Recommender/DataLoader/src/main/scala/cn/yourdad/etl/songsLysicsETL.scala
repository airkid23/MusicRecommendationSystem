package cn.yourdad.etl

import cn.yourdad.const.Const
import cn.yourdad.pojo.Song_Lysics
import cn.yourdad.utils.HBaseConnHelper
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{HTable, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.etl
 * @description: ${description}
 * @author: wzj
 * @create: 2020-09-28 10:22
 * */
object songsLysicsETL {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._
    val sc = spark.sparkContext
    val const = new Const
    val hbaseConnHelper = new HBaseConnHelper


    lazy val lysicsRDD = sc.textFile(const.SONG_LYSICS_INFO_PATH)
      .filter(line => {
        val arrays = line.split("\t")
        arrays.size == 2
      })
      .map(line => {
        val arrays = line.split("\t")
        Song_Lysics(arrays(0).trim, arrays(1).trim)
      })

    //foreachPartition创建一次数据库连接，批量插入数据
    lysicsRDD.foreachPartition( line => {
      //HBase配置
      val config = HBaseConfiguration.create()
      config.addResource(
        new Path(ClassLoader.getSystemResource("config/core-site.xml").toURI)
      )
      config.addResource(
        new Path(ClassLoader.getSystemResource("config/hbase-site.xml").toURI)
      )
      //设置HBase表名
      val myTable = new HTable(config, TableName.valueOf("playlist"))
      //对每一个分区写入歌词信息
      line.foreach { a =>
        var put = new Put(Bytes.toBytes(a.songID))
        put.addColumn(Bytes.toBytes("song"), Bytes.toBytes("lysics"), Bytes.toBytes(a.lysics))
        myTable.put(put)
      }
    })

    hbaseConnHelper.scanTable("playlist")
  }
}
