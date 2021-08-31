package cn.yourdad.controller;

import cn.yourdad.dao.SongMapper;
import cn.yourdad.pojo.PlaylistInfoBean;
import cn.yourdad.pojo.SingerInfoBean;
import cn.yourdad.pojo.Song;
import cn.yourdad.service.SingerService;
import cn.yourdad.service.SongService;
import cn.yourdad.util.HBaseConnUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.controller
 * @description:
 * @author: wzj
 * @create: 2020-10-03 13:07
 **/

@Controller
@RequestMapping("/song")
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;


    @RequestMapping("/getSongs/{songID}")
    public String getSongs(@PathVariable("songID") String songID, Model model){

        Song song = songService.getSongByID(songID);
        SingerInfoBean singer = singerService.getSingerInfoByID(song.getSingerID());
        model.addAttribute("singer", singer);
        model.addAttribute("song", song);
        String lysics = songService.getSongLysics(songID);
        List<PlaylistInfoBean> playlists = songService.getInvertedIndexPlaylists(songID);
        model.addAttribute("playlists", playlists.size() == 1? null: playlists);
        model.addAttribute("halfLysics", lysics.substring(0, (Integer)lysics.length()/8));
        model.addAttribute("lastLysics", lysics.substring((Integer)lysics.length()/8));

        return "song";
    }


}
