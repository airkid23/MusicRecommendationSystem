package cn.yourdad.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.dao
 * @description: 用户行为记录
 * @author: wzj
 * @create: 2020-10-06 13:00
 **/

@Mapper
public interface UserActionMapper {

    /**
     * 插入评分记录
     * @param uid
     * @param playlistID
     * @param rating
     * @param timestamp
     */
    @Insert(" insert into rating values(#{uid}, #{playlistID}, #{rating}, #{timestamp})")
    void insertPlaylistScore2Rating(String uid, String playlistID, Double rating, String timestamp);

    /**
     * 查询评分记录是否存在
     * @param uid
     * @param playlistID
     * @param rating
     * @return
     */
    @Deprecated
    @Select("select * from rating where uid = #{uid} and playlistID = #{playlistID}")
    boolean checkActionInRating(String uid, String playlistID, Double rating);

    /**
     * 更新评分记录
     * @param uid
     * @param playlistID
     * @param rating
     * @param timestamp
     */
    @Update("update rating set rate = #{rating} where uid = #{uid} and playlistID = #{playlistID}")
    void updatePlaylistScore2Rating(String uid, String playlistID, Double rating, String timestamp);
}
