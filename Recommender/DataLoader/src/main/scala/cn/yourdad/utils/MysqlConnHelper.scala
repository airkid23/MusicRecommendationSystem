package cn.yourdad.utils

import java.sql.{Connection, DriverManager}

/**
 * @program: BookRecommendationSystem
 * @package: cn.yourdad.utils
 * @description: ${description}
 * @author: wzj
 * @create: 2020-09-20 11:39
 * */
class MysqlConnHelper {

  def apply(): MysqlConnHelper = new MysqlConnHelper()


  def getMysqlConnection():Connection ={
    var conn: Connection = null
    Class.forName("com.mysql.jdbc.Driver")
    println("驱动加载成功")
    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useSSL=false","root","root")
    conn
  }


}
