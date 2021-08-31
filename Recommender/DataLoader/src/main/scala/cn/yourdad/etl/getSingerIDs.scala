package cn.yourdad.etl

import cn.yourdad.const.Const
import cn.yourdad.pojo.SongInfoBean
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.etl
 * @description: ${description}
 * @author: wzj
 * @create: 2020-10-09 11:56
 * */
object getSingerIDs {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._
    val sc = spark.sparkContext
    val const = new Const
    val hbaseConf = HBaseConfiguration.create()


    val singerIDDF = sc.textFile(const.SONG_INFO_PATH)
      .filter(line => {
        val arrs = line.split(" \\|\\+\\| ")
        arrs.size == 9
      })
      .map( line => {
        val arrs = line.split(" \\|\\+\\| ")
        SongInfoBean(arrs(0).trim, arrs(1).trim, arrs(2).trim, arrs(3).trim, arrs(4).trim, arrs(5).trim,
          arrs(6).trim, arrs(7).trim, arrs(8).trim)
      })
      .map(_.singerID)
      .toDF()

    singerIDDF.coalesce(1).write.option("header","false").csv("singerIDs.csv")

  }
}
