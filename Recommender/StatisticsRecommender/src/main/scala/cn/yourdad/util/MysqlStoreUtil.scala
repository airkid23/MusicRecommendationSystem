package cn.yourdad.util

import org.apache.spark.sql.DataFrame

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description: ${description}
 * @author: wzj
 * @create: 2020-09-27 13:01
 * */
class MysqlStoreUtil {
  def apply(): MysqlStoreUtil = new MysqlStoreUtil()

  def save2Mysql(df: DataFrame, tableName: String): Unit = {
    df.write
      .mode("overwrite")
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", tableName)
      .option("url","jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false")
      .option("user","root")
      .option("password","root")
      .save()
  }
}
