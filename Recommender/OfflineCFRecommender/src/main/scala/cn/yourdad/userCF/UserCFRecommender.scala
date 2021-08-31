package cn.yourdad.userCF

import java.util

import scala.collection.JavaConversions._
import cn.yourdad.mpping.{MappingID2RealID, RatingTransfrom}
import cn.yourdad.pojo.{UserSim, UserSimilarity}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.client.{HTable, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.distributed.{CoordinateMatrix, MatrixEntry, RowMatrix}
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.userCF
 * @description:  基于用户协同过滤产生推荐结果
 * @author: wzj
 * @create: 2020-10-12 22:35
 * */

object UserCFRecommender extends Serializable {

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
        put.addColumn(Bytes.toBytes("recs"), Bytes.toBytes("userCFRec"), Bytes.toBytes(a._2))
        myTable.put(put)
      }
    })
  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val sparkConf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[*]")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc = spark.sparkContext
    import spark.implicits._
    val idMapping = new MappingID2RealID
    //将CoordinateMatrix转换为RowMatrix计算两两用户的相似度。
    //由于RowMatrix只能就是那列的相似度，而用户数量是用行表示，因此CoordinateMatrix需要先转秩：

    //获取用户评分数据
    val ratingTransfrom = new RatingTransfrom
    val ratingRDD = ratingTransfrom.getTransRatingRDD().cache()
    //把评分数据的样例类转换成矩阵项
    val parseData: RDD[MatrixEntry] = ratingRDD .map{
      case Rating(user, product, rating) => MatrixEntry(user.toLong, product.toLong, rating)
    }
    val ratings = new CoordinateMatrix(parseData)
    //转换成行矩阵
    val matrix: RowMatrix = ratings.transpose().toRowMatrix()
    //调用API计算相似度
    val similarities: CoordinateMatrix = matrix.columnSimilarities()
    val list = new util.ArrayList[String]()

    similarities.entries.collect()
      .map( line => {
//        println(line.i + "\t"  + line.j + "\t" + line.value)
        //把矩阵索引转换成相应的用户id
      list.add(idMapping.getRealUID(line.i.toInt) + "->" + idMapping.getRealUID(line.j.toInt) + "->" + line.value.formatted("%.3f"))
    })

    val userSimRDD = sc.parallelize(list)
      .map( line => {
        val arrays = line.split("->")
        (arrays(0).trim, (arrays(1).trim, arrays(2).trim.toDouble))
      })
      .groupByKey()
      //提取相似度较高的前五位
      .map{
        case (uid, similarity) => UserSim(uid, similarity.toList.sortWith(_._2 > _._2).take(5).map( x =>UserSimilarity(x._1, x._2) ))
      }

    //获取每个用户评分≥3分的的歌单
    lazy val userProduct = ratings.entries.filter(_.value >= 3.0).map(line => {
      (idMapping.getRealUID(line.i.toInt), idMapping.getRealPlaylistID(line.j.toInt))
    })
      .groupByKey()
      .mapValues(_.toList.mkString("|"))
    //提取相似用户,数据格式为(待推荐用户,相似用户1|相似用户2|.....)
    val userSimUser = userSimRDD.map( line => {
      (line.uid1, line.userSimilarity.map( x => x.uid2).toList.mkString("|"))
    })
    //获取相似用户喜爱的歌单，通过相似用户与其喜爱的歌单做笛卡尔积
    val recRDD = userSimUser.cartesian(userProduct)
      //计算笛卡尔积后，过滤出相似用户喜爱的歌单
      .filter( item => (item._1._2.contains(item._2._1)  || item._1._1 == item._2._1))
      .map{
        case (user, userProduct) => (user._1, userProduct)
      }
      .groupByKey()
      .map(line => {
        val originList = new util.ArrayList[String]()
        val recList = new util.ArrayList[String]()
        //过滤掉听过的歌单
        for( a <- line._2){
          if (a._1 == line._1){
            val array = a._2.split("\\|")
            for (i <- array){
              originList.add(i)
            }
          }else{
            val arrays = a._2.split("\\|")
            for (i <- arrays){
              recList.add(i)
            }
          }
        }
        val list = new util.ArrayList[String]()
        for (a <- recList){
          if (!originList.contains(a)){
            list.add(a)
          }
        }
        (line._1, list.mkString("|"))
      })

    saveRecList2Hbase(recRDD)
//    //得到用户id=22对所有物品的评分
//    val ratingOfUser1: Array[Double] = ratings.entries.filter(_.i == 22).map( x => {
//      (x.j, x.value)
//    })
//      .sortBy(_._1)
//      .collect()
//      .map(_._2)
//      .toList
//      .toArray
//
//    println("得到用户admin对每种物品评分")
//    for (s <- ratingOfUser1){println(s)}
//
//    val avgRatingOfUser1: Double = ratingOfUser1.sum / ratingOfUser1.size
//    println("用户admin对所有物品的平均评分\t" + avgRatingOfUser1)
//
//
//    //其他用户对物品1的评分
//    val otherRatingsToItem1:Array[Double] = matrix.rows.collect()(0).toArray.drop(22)
//    println("其他用户对物品1的评分")
//    for (s <- otherRatingsToItem1){println(s)}
//
    //得到用户1相对其他用户的相似性(权重)，降序排序
//    val weights: Array[Double] = similarities.entries
//      .filter(_.i == 22)
//      .sortBy(_.value, false)
//      .map(_.value)
//      .collect()
//    println("用户admin相对与其他用户的相似性")
//    for (s <- weights){println(s)}

    spark.stop()
  }
}
