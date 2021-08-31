package cn.yourdad.pojo

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description: 歌曲信息bean
 * @author: wzj
 * @create: 2020-09-27 19:25
 * */
case class SongInfoBean(songID: String, songName:String, alID:String, publishTime:String, singerID: String,
                       commentCounts:String, heatCommentCounts:String, songSize: String, songLink:String)
