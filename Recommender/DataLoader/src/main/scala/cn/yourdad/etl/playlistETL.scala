package cn.yourdad.etl

import java.text.SimpleDateFormat
import java.util.Date

import cn.yourdad.const.Const
import cn.yourdad.pojo.PlaylistInfoBean
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.etl
 * @description: playlist etl模块
 * @author: wzj
 * @create: 2020-09-24 18:10
 * */
object playlistETL {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._
    val sc = spark.sparkContext
    val const = new Const
    val dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    val playlistRDD = sc.textFile(const.PLAYLIST_INFO_PATH)
    val playlistDF = playlistRDD
//      .filter( line => {
//      val arrs = line.split(" \\|\\=\\| ")
//       arrs.length == 13 & arrs(6).trim.toLong > 2500000.0
//    })
      .map( line => {
        val arrs = line.split(" \\|\\=\\| ")
        PlaylistInfoBean(arrs(0).trim, arrs(1).trim, arrs(2).trim,
          dateFormat.format(new Date(arrs(3).trim.toLong)), dateFormat.format(new Date(arrs(4).trim.toLong)),
          arrs(5).trim, arrs(6).trim, arrs(7).trim, arrs(8).trim,
          arrs(9).trim,arrs(10).trim, arrs(11).trim, arrs(12).trim)
      })
//      .map( line => (line.playlistID, line.playlistName))
      .toDF()
//    playlistDF.coalesce(1).write.option("header","false").option("delimiter", "¥").csv("playlist.csv")


    playlistDF.write
      .mode("overwrite")
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "playlist")
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .save()



  }
}
