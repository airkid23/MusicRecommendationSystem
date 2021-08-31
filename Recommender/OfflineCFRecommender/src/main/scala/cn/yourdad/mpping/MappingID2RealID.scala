package cn.yourdad.mpping

import java.sql.ResultSet

import cn.yourdad.util.MySQLConnUtil

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.mpping
 * @description: ${description}
 * @author: wzj
 * @create: 2020-10-12 20:09
 * */
class MappingID2RealID extends Serializable {

  def apply(): MappingID2RealID = new MappingID2RealID()

  def getRealUID(mappingID: Int): String = {

    val mysqlConnUtil =  new MySQLConnUtil
    val conn = mysqlConnUtil.getMysqlConnection()
    val sql = "select uid from uid_mapping where mappingID = '" +  mappingID.toString + "'"
    val stat = conn.createStatement()
    val rs:ResultSet = stat.executeQuery(sql)
    var str = ""
    while (rs.next()){
      str = rs.getString("uid")
    }
    rs.close()
    stat.close()
    conn.close()

    str
  }

  def getRealPlaylistID(mappingID: Int):String = {


    val mysqlConnUtil =  new MySQLConnUtil
    val conn = mysqlConnUtil.getMysqlConnection()
    val sql = "select playlistID from pid_mapping where mappingID = '" +  mappingID.toString + "'"
    val stat = conn.createStatement()
    val rs:ResultSet = stat.executeQuery(sql)
    var str = ""
    while (rs.next()){
      str = rs.getString("playlistID")
    }
    rs.close()
    stat.close()
    conn.close()

    str
  }

}
