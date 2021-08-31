package cn.yourdad.offlineLFM

import java.util

import cn.yourdad.mpping.{MappingID2RealID, RatingTransfrom}
import cn.yourdad.pojo.{PlaylistRecs, Recommendation, UserRecs}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{HTable, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.jblas.DoubleMatrix

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.offlineLFM
 * @description: 隐语义模型预测评分
 * @author: wzj
 * @create: 2020-10-11 11:07
 * */
object LFMRecommender {

  val USER_MAX_RECOMMENDATION = 5
  def saveRecList2Hbase(recRDD: RDD[(String, String)] , qulifier: String) = {

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
        put.addColumn(Bytes.toBytes("recs"), Bytes.toBytes(qulifier), Bytes.toBytes(a._2))
        myTable.put(put)
      }
    })
  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val sparkConf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[*]")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val ratingTransfrom = new RatingTransfrom
    val ratingRDD = ratingTransfrom.getTransRatingRDD().cache()
    val idMapping = new MappingID2RealID

    val userRDD = ratingRDD.map(_.user).distinct()
    val playlistRDD = ratingRDD.map(_.product).distinct()

    //计算得到较优的ALS参数
    val (rank, iterations, lambda) = (150, 10, 0.5)
    //训练模型
    val model = ALS.train(ratingRDD, rank, iterations, lambda)
    //用用户与歌单做笛卡尔积作为测试数据
    val userPlaylist = userRDD.cartesian(playlistRDD)
    val preRatings = model.predict(userPlaylist)

    val LFMRecs = preRatings
      .filter( _.rating > 0)
      .map(rating => (rating.user, (rating.product, rating.rating)))
      .groupByKey()
      //对预测评分进行排序，并取评分较高的前五位推荐结果
      .map{
        case (uid, recs) =>UserRecs(uid, recs.toList.sortWith(_._2 > _._2)
          .take(USER_MAX_RECOMMENDATION).map(x => Recommendation(x._1, x._2)))
      }

      val recResult = LFMRecs
      .map{
        case UserRecs(uid, recommendation) => (uid, recommendation.map(_.playlistID))
      }
        .map( line => {
          val list = new util.ArrayList[String]()
          for (a <- line._2){
            list.add(idMapping.getRealPlaylistID(a))
          }
          (idMapping.getRealUID(line._1), list.toArray.toList.mkString("|"))
        })

    saveRecList2Hbase(recResult, "LFMRec")

println("=============================================================================================================")
    //提取每个歌单的隐性特征
    val playlistFeatures = model.productFeatures
      .map{
        case(playlistID, features) => (playlistID, new DoubleMatrix(features))
      }

    //根据隐性特征计算相似度， 进行相似歌单推荐
    val playlistRecByLFMSim = playlistFeatures.cartesian(playlistFeatures)
      .filter{
        case(playlist1, playlist2) => playlist1._1 != playlist2._1
      }
      .map{
        case(playlist1, playlist2) => {
          val simScore = this.consinsim(playlist1._2, playlist2._2)
          (playlist1._1, (playlist2._1, simScore))
        }
      }
      .filter(_._2._2 > 0.6)
      .groupByKey()
      .map{
        case (playlistID, item) => PlaylistRecs(playlistID, item.toList.sortWith(_._2 > _._2).map( x => Recommendation(x._1, x._2)))
      }
      val latentFeatureRecRDD = playlistRecByLFMSim.map{
          case PlaylistRecs(pid, recommendation) => (idMapping.getRealPlaylistID(pid), recommendation.map(line => {
        (idMapping.getRealPlaylistID(line.playlistID), line.score)
      }).mkString("|"))
    }

    saveRecList2Hbase(latentFeatureRecRDD, "lfSim")
  }

  //求向量余弦相似度
  def consinsim(movie1: DoubleMatrix, movie2: DoubleMatrix):Double = {
    movie1.dot(movie2) / (movie1.norm2() * movie2.norm2())
  }

}
