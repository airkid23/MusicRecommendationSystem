package cn.yourdad.pojo

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad
 * @description: ${description}
 * @author: wzj
 * @create: 2020-09-27 12:52
 * */
case class PlaylistInfoBean(playlistID:String, creatorID: String, playlistName: String, createTime: String, updateTime: String,
                            SongsCount: String, playCount: String, shareCounts: String, commentsCount: String, collectCount: String, tag: String,
                            playlistCoverUrl: String, desc: String)
