package cn.yourdad.mpping

import java.sql.ResultSet

import cn.yourdad.util.MySQLConnUtil
import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.mpping
 * @description: ${description}
 * @author: wzj
 * @create: 2020-10-11 15:25
 * */
class RatingTransfrom extends Serializable {

  def apply(): RatingTransfrom = new RatingTransfrom()

  def getTransRatingRDD(): RDD[Rating] = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getSimpleName)
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._
    
    val ratingRDD = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "rating")
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .load()
      .rdd
      .map(
        line => {
          Rating(getMappingUID(line.getAs[String]("uid")), getMappingPlaylistID(line.getAs[String]("playlistID")), line.getAs[Double]("rate"))
        }
      )
    ratingRDD
  }

  def getMappingUID(uid:String): Int ={
    val mysqlConnUtil =  new MySQLConnUtil
    val conn = mysqlConnUtil.getMysqlConnection()
    val sql = "select mappingID from  uid_mapping where uid = '" + uid + "'"
    val stat = conn.createStatement()
    val rs:ResultSet = stat.executeQuery(sql)
    var str = 0;
    while (rs.next()){
     str  = rs.getInt("mappingID")
    }
    rs.close()
    stat.close()
    conn.close()

    str
  }

  def getMappingPlaylistID(playlistID: String): Int ={
    val mysqlConnUtil =  new MySQLConnUtil
    val conn = mysqlConnUtil.getMysqlConnection()
    val sql = "select mappingID from  pid_mapping where playlistID = '" + playlistID + "'"
    val stat = conn.createStatement()
    val rs:ResultSet = stat.executeQuery(sql)
    var str = 0;
    while (rs.next()){
      str  = rs.getInt("mappingID")
    }
    rs.close()
    stat.close()
    conn.close()

    str
  }

}
