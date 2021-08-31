package cn.yourdad.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.pojo
 * @description: 用户bean
 * @author: wzj
 * @create: 2020-09-28 19:32
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String uid;
    private String uname;
    private Integer sex;
    private Long registTs;
    private String password;
    private Long phoneNumber;

}
