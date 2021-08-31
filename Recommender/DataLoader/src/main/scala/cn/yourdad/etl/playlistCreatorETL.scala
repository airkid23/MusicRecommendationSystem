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
 * @description: ${description}
 * @author: wzj
 * @create: 2020-10-09 22:32
 * */

case class CreatorInfo(creatorID: String, creatorName: String)

object playlistCreatorETL {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._
    val sc = spark.sparkContext
    val const = new Const

    val playlistRDD = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "playlist")
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .load()
      .as[PlaylistInfoBean]
      .rdd
      .toDF()


//    val creatorID = playlistRDD.map( _.creatorID).toDF()
//    creatorID.coalesce(1).write.option("header","false").csv("creator.csv")
    val creatorRDD = sc.textFile(const.CREATOR_INFO_PAH)
   .map( line => {
      val arrays = line.split("\t")
      CreatorInfo(arrays(0).trim, arrays(1).trim)
    })
  .distinct(1)
  .toDF()

    val newPlaylistDF = playlistRDD.join(creatorRDD, "creatorID")

    println(newPlaylistDF.count())

    newPlaylistDF.show(50)

//    creatorRDD.write
//      .mode("overwrite")
//      .format("jdbc")
//      .option("driver", "com.mysql.jdbc.Driver")
//      .option("dbtable", "creatorInfo")
//      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
//      .option("user","root")
//      .option("password","root")
//      .save()




  }

}
