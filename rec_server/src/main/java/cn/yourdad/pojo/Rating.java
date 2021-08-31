package cn.yourdad.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description:
 * @author: wzj
 * @create: 2020-10-10 19:43
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    private String uid;
    private String playlistID;
    private Double rate;
    private String rate_time;
}
