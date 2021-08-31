package cn.yourdad.playlistTagSim

import java.util

import scala.collection.JavaConversions._
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{HTable, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkConf
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.ml.linalg.SparseVector
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.jblas.DoubleMatrix
import redis.clients.jedis.Jedis


/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.playlist
 * @description: 通过TF-IDF tags计算歌单相似度,基于内容协同过滤
 * @author: wzj
 * @create: 2020-09-26 13:28
 * */

case class PlaylistInfoBean(playlistID:String, creatorID: String, playlistName: String, createTime: String, updateTime: String,
                            SongsCount: String, playCount: String, shareCounts: String, commentsCount: String, collectCount: String, tag: String,
                            playlistCoverUrl: String, desc: String)

case class Recommendation(playlistID:String, simScore: Double)

case class PlaylistRecs(playlistID: String, recs: Seq[Recommendation])

object playlistSim {

  // 求向量余弦相似度
  def consinSim(playlist1: DoubleMatrix, playlist2: DoubleMatrix):Double ={
    playlist1.dot(playlist2) / ( playlist1.norm2() * playlist2.norm2() )
  }

  def strTrans(string: String): List[String] = {
    val list = string.replaceAll(",","|").replaceAll("'","").replace("[","").replace("]","")
      .replaceAll("\\| ",",").replaceAll(" ", "").split(",").toList

    list
  }


//  if (jedis.exists("uid:" + uid) && jedis.llen("uid:" + uid) >= Constant.REDIS_PLAYLIST_RATING_QUEUE_SIZE) jedis.rpop("uid:" + uid)
//  jedis.lpush("uid:" + uid, pid + ":" + rating)
  def storeSim2Redis(playlistRecs: RDD[PlaylistRecs]) = {
    val rdd= playlistRecs.map(item => {

      (item.playlistID, item.recs.map( x => (x.playlistID, x.simScore)))
    })
  rdd.foreachPartition( line => {

    val jedis = new Jedis("localhost")

    line.foreach( x => {
      for (a <- x._2){
        jedis.lpush("tagsim:" + x._1, a._1 + ":" + a._2.toString)
      }
    })
  })
  }

  def storeSim2Mysql(playlistTagSim: DataFrame) = {
    playlistTagSim.write
      .mode("overwrite")
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "playlistTagSim")
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .save()
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

      val idTagsDF = playlistRDD.map( line => (line.playlistID, strTrans(line.tag).mkString(" ")))
        .toDF("playlistID", "tag")
        .cache()


   // 核心部分：用TF-IDF从内容信息中提取电影特征向量

    //创建一个分词器，默认按空格分词
    val tokenizer = new Tokenizer().setInputCol("tag").setOutputCol("words")
    //用分词器对原始数据做转化，得到样例数据如 5058973317 流行 学习 兴奋 [流行,学习,兴奋]
    val wordsData = tokenizer.transform(idTagsDF)
    //引入HashingTF工具，可以把一个词语序列转换成对应的词频
    val hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(51)
    val featurizedData = hashingTF.transform(wordsData)
    //引入IDF工具，可以得到IDF模型
    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    //训练idf模型，得到每个词的逆文档频率
    val idfModel = idf.fit(featurizedData)
    //用模型对原始数据进行处理，得到文档中每个词的tf-idf，作为新的特征向量
    val rescaleData = idfModel.transform(featurizedData)

    val playlistFeatures = rescaleData.map(
      row => (row.getAs[String]("playlistID"), row.getAs[SparseVector]("features").toArray )
    )
      .rdd
      .map(
        x => (x._1, new DoubleMatrix(x._2))
      )

    //做笛卡尔积，计算相似度
    val playlistRecs = playlistFeatures.cartesian(playlistFeatures)
      .filter {
            //过滤自己
        case (p1, p2) => p1._1 != p2._1
      }
      .map{
        case(p1, p2) => {
          val simScore = this.consinSim(p1._2, p2._2)
          (p1._1, Recommendation(p2._1, simScore))
        }
      }
      .filter(_._2.simScore > 0.6)
      .groupByKey()
      .map{
        case(pid, items) => PlaylistRecs(pid, items.toList.sortWith(_.simScore > _.simScore).map( x=> Recommendation(x.playlistID, x.simScore.formatted("%.2f").toDouble)).take(5))
      }


    val playlistTagSim = playlistRecs.map( line => {
      (line.playlistID, line.recs.map(x => (x.playlistID, x.simScore)).mkString("|").replaceAll("\\(", "")
      .replaceAll("\\)", "").replaceAll(",", ":"))
    })
      .toDF("playlistID", "tagSim")

    storeSim2Mysql(playlistTagSim)



//    storeSim2Redis(playlistRecs)
   // 将推荐列表写入hbase

//    playlistRecs.foreachPartition( line => {
//      val config = HBaseConfiguration.create()
//      config.addResource(
//        new Path("/home/wzj/IdeaProjects/MusicRecommendationSystem/Recommender/DataLoader/src/main/resources/config/core-site.xml")
//      )
//      config.addResource(
//        new Path("/home/wzj/IdeaProjects/MusicRecommendationSystem/Recommender/DataLoader/src/main/resources/config/hbase-site.xml")
//      )
//      val myTable = new HTable(config, TableName.valueOf("playlist"))
//      line.foreach { a =>
//        var put = new Put(Bytes.toBytes(a.playlistID))
//        put.addColumn(Bytes.toBytes("recs"), Bytes.toBytes("recPl"), Bytes.toBytes(a.recs.toString()))
//        myTable.put(put)
//      }
//    })


  }
}
