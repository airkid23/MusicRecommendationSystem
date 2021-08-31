package cn.yourdad.controller;

import cn.yourdad.pojo.ItemCFRecommendation;
import cn.yourdad.pojo.PlaylistInfoBean;
import cn.yourdad.pojo.Rating;
import cn.yourdad.pojo.User;
import cn.yourdad.service.PlaylistService;
import cn.yourdad.service.RatingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.controller
 * @description:
 * @author: wzj
 * @create: 2020-09-23 18:10
 **/

@Controller
public class IndexController {

    @Autowired
    PlaylistService playlistService;

    @Autowired
    RatingService ratingService;

    @RequestMapping({"/index", "/"})
    public String toIndex(Model model, HttpSession session){

        List<PlaylistInfoBean> playlistInfoBeans = new ArrayList<>();
        List<String> playlistIDs = playlistService.getPlMostPlay();
        Integer count = 0;
        for (String playlistID: playlistIDs
        ) {
            if (count == 8 )break;
            count ++;
            playlistInfoBeans.add(playlistService.getPlaylistByID(playlistID));
        }
        model.addAttribute("playlists", playlistInfoBeans);

        User user = (User) session.getAttribute("user");
        String uid = user.getUid();
        List<PlaylistInfoBean> userCFPL = new ArrayList<>();
        userCFPL = playlistService.getUserCFRecs(uid);
        Rating isNewUser = ratingService.findRatingByUid(uid);
        if (isNewUser == null){

        }else{
            model.addAttribute("userCFRec", (userCFPL.size() > 5)?userCFPL.subList(0, 5): userCFPL);
            List<PlaylistInfoBean> LFMRecPL = new ArrayList<>();
            LFMRecPL = playlistService.getLFMRecs(uid);
            model.addAttribute("lfmRec", (LFMRecPL.size()>5)?LFMRecPL.subList(0, 5): LFMRecPL);
            ItemCFRecommendation itemCFRecommendation = playlistService.getItemCFRecByUID(uid);
            model.addAttribute("itemCF", itemCFRecommendation.getPlaylists());
            model.addAttribute("pled", playlistService.getPlaylistName(itemCFRecommendation.getPlayed()));
            model.addAttribute("pledScore", itemCFRecommendation.getPledScore());
        }
        return "index";
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/toRegister")
    public String toRegister(){
        return "register";
    }

    @RequestMapping("/toPlaylist")
    public String toPlaylist(Model model, @RequestParam(defaultValue = "1", value = "pageNum")Integer pageNum){
        PageHelper.startPage(pageNum, 20);
        List<PlaylistInfoBean> playlist = playlistService.getPlaylist();
        PageInfo<PlaylistInfoBean> pageInfo = new PageInfo<>(playlist);
        model.addAttribute("pageInfo", pageInfo);
        return "playlist";
    }

    @RequestMapping("/toRanking")
    public String toRanking(){
        return "redirect:/playlist/getPlMostPlay";
    }

}
