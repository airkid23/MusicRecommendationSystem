package cn.yourdad.etl

import java.io.{File, PrintWriter}
import java.util
import java.util.Date

import cn.yourdad.const.Const
import cn.yourdad.pojo.PlaylistInfoBean
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions.asScalaSet
import scala.util.matching.Regex
import scala.collection.mutable.Set

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.etl
 * @description: 对歌单的标签提取
 * @author: wzj
 * @create: 2020-09-26 10:16
 * */
object playlistTagETL {

  def strTrans(string: String): List[String] = {
    val list = string.replaceAll(",","|").replaceAll("'","").replace("[","").replace("]","")
      .replaceAll("\\| ",",").split(",").toList
    list
  }
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._

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

    val tagsSet = new util.HashSet[String]()

    //对标签内容提取并去重
    val TagsRDD = playlistRDD.map(line => (strTrans(line.tag)))
      .filter(!_.head.isEmpty)
      .collect()
      .foreach( line => {
        for (i <- line){
          tagsSet.add(i)
        }
      })
println(tagsSet.size())
//    //构建tag对应数字，进行矩阵运算
//    val tagsMap = new util.HashMap[String, Int]()
//    val it = tagsSet.iterator()
//    var count = 0
//    while (it.hasNext){
//      count = count + 1
//      tagsMap.put(it.next(), count)
//    }
//
//    //将每首歌单的tags转换成一维矩阵
//    val idTagsRDD = playlistRDD.map( line => (line.playlistID, strTrans(line.tag)))
//      .map( line => {
//        var list = new util.ArrayList[Double]()
//        for (i <- line._2){
//          list.add(tagsMap.get(i).toDouble)
//        }
//
//        (line._1, list)
//      })
//    //      .map(
//    //        x => (x._1, new DoubleMatrix(x._2))
//    //      )
//
//    idTagsRDD.foreach(println)

    //    idTagsRDD.foreach(println)
    //    output : (5058973317,[14.0, 17.0, 39.0])

  }

}
