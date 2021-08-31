package cn.yourdad.dao;

import cn.yourdad.pojo.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.dao
 * @description:
 * @author: wzj
 * @create: 2020-10-03 13:09
 **/

@Mapper
public interface SongMapper {


    @Select({
            "<script>",
            "select",
            "*",
            "from songs",
            "where songID in",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<Song> getSongs(@Param("ids") List<String> ids);

    @Select("select * from songs where songID = #{songID}")
    Song getSongByID(String songID);
}
