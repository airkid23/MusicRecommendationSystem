package cn.yourdad.dao;

import cn.yourdad.pojo.PlaylistInfoBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.dao
 * @description:
 * @author: wzj
 * @create: 2020-09-25 13:34
 **/

@Mapper
public interface PlaylistMapper {

    /**
     * 获取所有歌单
     * @return
     */
    @Select("select * from playlist")
    List<PlaylistInfoBean> getPlaylist();

    /**
     * 获取歌单id
     * @return
     */
    @Select("select playlistID from plMostPlay")
    List<String> getPlMostPlay();

    /**
     * 获取歌单所有id
     * @param playlistID
     * @return
     */
    @Select("select songsID from songsID where playlistID = #{playlistID}")
    String getSongsID(String playlistID);

    /**
     * 根据歌单id获取歌单名
     * @param playlistID
     * @return
     */
    @Select("select playlistName from playlist where playlistID = #{playalistID}")
    String getPlaylistName(String playlistID);

    /**
     * 根据id获取歌单信息
     * @param playlistID
     * @return
     */
    @Select("select * from playlist where playlistID = #{playlistID}")
    PlaylistInfoBean getPlaylistByID(String playlistID);

    /**
     * 根据列表获取推荐数据
     * @param ids
     * @return
     */
    @Select({
            "<script>",
            "select",
            "*",
            "from playlist",
            "where playlistID in",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<PlaylistInfoBean> getPlaylistInfoByList( @Param("ids") List<String> ids);

    /**
     * 获取歌单创建者昵称
     * @param creatorID
     * @return
     */
    @Select("select creatorName from creatorInfo where creatorID = #{creatorID}")
    String getCreatorNameByID(String creatorID);

}
