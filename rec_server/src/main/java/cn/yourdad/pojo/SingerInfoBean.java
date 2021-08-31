package cn.yourdad.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description: 歌手信息bean
 * @author: wzj
 * @create: 2020-10-09 17:49
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingerInfoBean {

    private String singerID;
    private String singerName;
    private String singerImg;

}
