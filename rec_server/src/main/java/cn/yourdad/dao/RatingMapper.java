package cn.yourdad.dao;

import cn.yourdad.pojo.Rating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.dao
 * @description: 检查是否新用户或没有评过分的
 * @author: wzj
 * @create: 2020-10-16 13:29
 **/
@Mapper
public interface RatingMapper {

    @Select("select * from rating where uid = #{uid} limit 1")
    public Rating findRatingByUid(String uid);
}
