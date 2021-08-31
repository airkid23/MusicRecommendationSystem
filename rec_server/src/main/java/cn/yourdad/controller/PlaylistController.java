package cn.yourdad.controller;

import cn.yourdad.pojo.PlaylistInfoBean;
import cn.yourdad.pojo.Result;
import cn.yourdad.pojo.Song;
import cn.yourdad.service.PlaylistService;
import cn.yourdad.service.SongService;
import cn.yourdad.util.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.controller
 * @description: 歌单业务controller
 * @author: wzj
 * @create: 2020-09-25 10:50
 **/

@Controller
@RequestMapping("/playlist")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private SongService songService;

    /**
     * 获取全部歌单信息
     * @return
     * @throws SQLException
     */
    @Deprecated
    @ResponseBody
    @RequestMapping("/getplaylist")
    public Result getPlaylist() throws SQLException {

        return ResultUtil.success(playlistService.getPlaylist());
    }

    /**
     * 通过歌单id进入歌单详情页
     * @param playlistID
     * @return
     */
    @RequestMapping("/getPlaylistInfo/{playlistID}/")
    @ResponseBody
    public Result getPlaylistInfo(@PathVariable("playlistID") String playlistID){
        System.out.println("进入" + playlistID + "页");
        return ResultUtil.success();
    }

    /**
     * 获取最多播放排行榜
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping("/getPlMostPlay")
    public String getPlMostPlay(Model model){

        List<PlaylistInfoBean> playlistInfoBeans = new ArrayList<>();
        List<String> playlistIDs = playlistService.getPlMostPlay();
        for (String playlistID: playlistIDs
             ) {
            playlistInfoBeans.add(playlistService.getPlaylistByID(playlistID));
        }
        model.addAttribute("playlists", playlistInfoBeans);
        return "ranking";
    }


    /**
     * 获取单一歌单信息
     * @param playlistID
     * @param model
     * @return
     */
    @RequestMapping("/getSinglePlaylistInfo/{playlistID}")
    public String getSinglePlaylistInfo(@PathVariable("playlistID") String playlistID, Model model) throws Exception {

        List<String> ids = playlistService.getSongsID(playlistID);
        List<Song> songs = songService.getSongs(ids);
        List<PlaylistInfoBean> simPlaylists = playlistService.getSimPlaylist(playlistID, 5);
        List<Double> simScore = playlistService.getPlaylistSimScoreByPID(playlistID, 5);
        model.addAttribute("simPlaylist", simPlaylists);
        model.addAttribute("simScore", simScore);
        model.addAttribute("songs", songs);
        PlaylistInfoBean playlist = playlistService.getPlaylistByID(playlistID);
        String creatorName = playlistService.getCreatorNameByID(playlist.getCreatorID());
        model.addAttribute("creatorName", creatorName);
        model.addAttribute("playlist", playlist);

        return "singlePlaylist";
    }

    /**
     * 保存用户评分行为
     * @param uid
     * @param playlistID
     * @param rating
     * @param session
     */
    @PostMapping("/saveUserAction")
    public void insertPlaylistRating2Hbase(@RequestParam("uid") String uid, @RequestParam("playlistID") String playlistID,
                                           @RequestParam("rating") int rating, HttpSession session){

        if (session.getAttribute("user") != null){
            playlistService.insertPlaylistRating2Hbase(uid, playlistID, rating);
        }

    }


}
