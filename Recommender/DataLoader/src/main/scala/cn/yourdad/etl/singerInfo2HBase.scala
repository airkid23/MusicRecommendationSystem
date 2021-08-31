package cn.yourdad.etl

import cn.yourdad.const.Const
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
 * @description: 将歌手信息导入hbase
 * @author: wzj
 * @create: 2020-10-09 16:54
 * */

case class SingerInfo(SingerID: String, SingerName:String, SingerImg: String)

object singerInfo2HBase {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._
    val sc = spark.sparkContext
    val const = new Const
    val hbaseConnHelper = new HBaseConnHelper

    lazy val singerInfoRDD = sc.textFile(const.SINGER_INFO_PAH)
    val singerInfoDF = singerInfoRDD.filter(line => {
      val arrays = line.split("\t")
      arrays.size == 3
    })
      .map(line => {
        val arrays = line.split("\t")
        SingerInfo(arrays(0).trim, arrays(1).trim, arrays(2).trim)
      })


  singerInfoDF.foreachPartition( line => {
    val config = HBaseConfiguration.create()
    config.addResource(
      new Path(ClassLoader.getSystemResource("config/core-site.xml").toURI)
    )
    config.addResource(
      new Path(ClassLoader.getSystemResource("config/hbase-site.xml").toURI)
    )

    val myTable = new HTable(config, TableName.valueOf("playlist"))
    line.foreach { a =>
      var put = new Put(Bytes.toBytes(a.SingerID))
      put.addColumn(Bytes.toBytes("singer"), Bytes.toBytes("singerID"), Bytes.toBytes(a.SingerID))
      put.addColumn(Bytes.toBytes("singer"), Bytes.toBytes("singerName"), Bytes.toBytes(a.SingerName))
      put.addColumn(Bytes.toBytes("singer"), Bytes.toBytes("singerImg"), Bytes.toBytes(a.SingerImg))
      myTable.put(put)
    }
  })

    hbaseConnHelper.scanTable("playlist")


  }
}
