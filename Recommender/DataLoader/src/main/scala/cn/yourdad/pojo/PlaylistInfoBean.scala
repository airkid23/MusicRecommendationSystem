package cn.yourdad.pojo

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description:  # 歌单ID，创建者ID，名字，创建时间，更新时间，包含音乐数，播放次数，分享次数，评论次数，收藏次数，标签，歌单封面，描述
 * @author: wzj
 * @create: 2020-09-24 18:17
 * */

case class PlaylistInfoBean(playlistID:String, creatorID: String, playlistName: String, createTime: String, updateTime: String,
                        SongsCount: String, playCount: String, shareCounts: String, commentsCount: String, collectCount: String, tag: String,
                         playlistCoverUrl: String, desc: String)
