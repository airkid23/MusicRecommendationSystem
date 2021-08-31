package cn.yourdad.controller;

import cn.yourdad.pojo.Result;
import cn.yourdad.service.UserActionService;
import cn.yourdad.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.controller
 * @description: 用户行为控制器
 * @author: wzj
 * @create: 2020-10-06 13:08
 **/

@Controller
public class UserActionController {

    @Autowired
    private UserActionService userActionService;

    @ResponseBody
    @RequestMapping("/playlistRating")
    public Result insertPlaylistScore2Rating(@RequestParam("uid") String uid,
           @RequestParam("playlistID") String playlistID, @RequestParam("rating") Double rating){

        userActionService.insertPlaylistScore2Rating(uid, playlistID, rating);
        return ResultUtil.success();
    }


}
