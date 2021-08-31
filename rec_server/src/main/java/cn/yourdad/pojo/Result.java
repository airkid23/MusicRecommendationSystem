package cn.yourdad.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description:
 * @author: wzj
 * @create: 2020-09-23 18:57
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    private Integer code;

    private String msg;

    private T data;

}