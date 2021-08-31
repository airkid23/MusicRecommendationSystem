package cn.yourdad.util

import java.sql.{Connection, DriverManager}

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description: ${description}
 * @author: wzj
 * @create: 2020-10-11 13:51
 * */
class MySQLConnUtil {

  def apply(): MySQLConnUtil = new MySQLConnUtil()


  def getMysqlConnection():Connection ={
    var conn: Connection = null
    Class.forName("com.mysql.jdbc.Driver")
//    println("驱动加载成功")
    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false","root","root")
    conn
  }

}
