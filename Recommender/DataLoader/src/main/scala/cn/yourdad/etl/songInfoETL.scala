package cn.yourdad.etl

import cn.yourdad.const.Const
import cn.yourdad.pojo.SongInfoBean
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.etl
 * @description: 对歌曲进行etl
 * @author: wzj
 * @create: 2020-09-27 18:37
 * */
object songInfoETL {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("playlistETL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._
    val sc = spark.sparkContext
    val const = new Const
    val hbaseConf = HBaseConfiguration.create()


    //歌单数据格式：32688174 |+| 男儿当自强 |+| 3169436 |+| 1255276800007 |+| 2118 |+| 6548 |+| 15 |+| 4209439 |+|
    // https://m7.music.126.net/20200925e22/ymusic/0992/b690/f4b6/c2fb4.mp3
    val songInfoDF = sc.textFile(const.SONG_INFO_PATH)
      //分离歌单数据中的“|+|”，反斜杠符号用于转义，过滤出9个元素的歌单
      .filter(line => {
        val arrs = line.split(" \\|\\+\\| ")
        arrs.size == 9
      })
      //把上面得到的RDD转换样例类，方便以后处理
      .map( line => {
        val arrs = line.split(" \\|\\+\\| ")
        SongInfoBean(arrs(0).trim, arrs(1).trim, arrs(2).trim, arrs(3).trim, arrs(4).trim, arrs(5).trim,
          arrs(6).trim, arrs(7).trim, arrs(8).trim)
      })
      .toDF()

    songInfoDF.write
      .mode("overwrite")
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "songs")
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .save()

  }

}
