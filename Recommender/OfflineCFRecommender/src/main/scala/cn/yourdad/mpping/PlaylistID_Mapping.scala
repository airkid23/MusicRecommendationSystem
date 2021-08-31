package cn.yourdad.mpping

import java.sql.ResultSet
import java.util

import cn.yourdad.util.MySQLConnUtil

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.mpping
 * @description: ${description}
 * @author: wzj
 * @create: 2020-10-11 13:40
 * */
class PlaylistID_Mapping {

  def apply(): PlaylistID_Mapping = new PlaylistID_Mapping()

  def insertUserID_Mapping(uid: String, mappingID: Int): Unit ={
    val mysqlConnUtil =  new MySQLConnUtil
    val conn = mysqlConnUtil.getMysqlConnection()
    val sql = "insert into pid_mapping values (?, ?)"
    val prep = conn.prepareStatement(sql)
    prep.setString(1, uid)
    prep.setInt(2, mappingID)
    prep.execute()

    prep.close()
    conn.close()

  }

  def truncateTable(): Unit ={
    val mysqlConnUtil =  new MySQLConnUtil
    val conn = mysqlConnUtil.getMysqlConnection()

    val sql = "truncate pid_mapping"
    val prep = conn.prepareStatement(sql)
    prep.execute()
    prep.close()
    conn.close()
  }

  def trans(): Unit = {

    truncateTable()
    val mysqlConnUtil =  new MySQLConnUtil
    val conn = mysqlConnUtil.getMysqlConnection()

    val lst = new util.ArrayList[String]()
    val sql = "select distinct(playlistID) from rating"
    val stat = conn.createStatement()
    val rs:ResultSet = stat.executeQuery(sql)
    while (rs.next()){
      lst.add(rs.getString("playlistID"))
    }
    val count = lst.size()
    var a = 1
    for (a <- 1 to count){
      insertUserID_Mapping( lst.get(a-1), a)
    }

    rs.close()
    stat.close()
    conn.close()
  }
}
