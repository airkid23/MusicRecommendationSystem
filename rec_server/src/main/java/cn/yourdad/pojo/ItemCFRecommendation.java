package cn.yourdad.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description:
 * @author: wzj
 * @create: 2020-10-15 17:38
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCFRecommendation {

    private String played;
    private Double pledScore;
    private List<PlaylistInfoBean> playlists;

}
