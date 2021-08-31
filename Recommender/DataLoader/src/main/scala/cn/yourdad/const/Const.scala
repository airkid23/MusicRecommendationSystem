package cn.yourdad.const

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.const
 * @description: 常量类
 * @author: wzj
 * @create: 2020-08-05 21:28
 **/
class Const {

  def apply(): Const = new Const()

  val PLAYLIST_INFO_PATH = this.getClass.getClassLoader.getResource("playlist/playlist_info.txt").toString
  val SONG_INFO_PATH = this.getClass.getClassLoader.getResource("song/songs_info.txt").toString
  val SONG_LYSICS_INFO_PATH = this.getClass.getClassLoader.getResource("song/songs_lysics.txt").toString
  val SONGS_ID_PATH = this.getClass.getClassLoader.getResource("playlist/idsInPlaylist.txt").toString
  val SINGER_INFO_PAH = this.getClass.getClassLoader.getResource("singer/singerInfo.txt").toString
  val CREATOR_INFO_PAH = this.getClass.getClassLoader.getResource("playlist/creatorInfo.txt").toString
}
