package cn.yourdad.etl

import cn.yourdad.const.Const
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.etl
 * @description: 将所有歌单id提取并存储到mysql
 * @author: wzj
 * @create: 2020-10-02 20:39
 * */

case class Playlist_SongsID(playlistID: String, songsID: String )
object allSongsID {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._
    val sc = spark.sparkContext
    val const = new Const


    val SongsIDDF = sc.textFile(const.SONGS_ID_PATH)
      .map( line => {
        val arrs = line.split("\t")
        Playlist_SongsID(arrs(0).trim, arrs(1).trim)
      })
      .toDF()


    SongsIDDF.write
      .mode("overwrite")
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "songsID")
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .save()

  }

}
