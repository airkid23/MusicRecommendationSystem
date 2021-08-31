package cn.yourdad.offlineLFM

import cn.yourdad.mpping.{PlaylistID_Mapping, UID_Mapping}

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.offlineLFM
 * @description: ${description}
 * @author: wzj
 * @create: 2020-10-17 15:42
 * */
object InitMappingTable {
  def main(args: Array[String]): Unit = {

    val playlistID_Mapping = new PlaylistID_Mapping
    playlistID_Mapping.trans()
    val uid_mapping = new UID_Mapping
    uid_mapping.trans()
    println("trans mapping over")
  }
}
