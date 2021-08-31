package cn.yourdad.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description:  playlist bean
 * @author: wzj
 * @create: 2020-09-25 13:36
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistInfoBean {

    String playlistID;
    String creatorID;
    String playlistName;
    String createTime;
    String updateTime;
    String SongsCount;
    String playCount;
    String shareCounts;
    String commentsCount;
    String collectCount;
    String tag;
    String playlistCoverUrl;
    String desc;
}
