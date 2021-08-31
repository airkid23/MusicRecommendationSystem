package cn.yourdad.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description: song bean
 * @author: wzj
 * @create: 2020-10-03 13:10
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Song {

    private String songID;
    private String songName;
    private String alID;
    private String publishTime;
    private String singerID;
    private String commentCounts;
    private String heatCommentCounts;
    private String songSize;
    private String songLink;

}
