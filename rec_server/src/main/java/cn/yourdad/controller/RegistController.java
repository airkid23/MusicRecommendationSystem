package cn.yourdad.controller;

import cn.yourdad.pojo.Result;
import cn.yourdad.pojo.User;
import cn.yourdad.service.UserService;
import cn.yourdad.util.MD5Util;
import cn.yourdad.util.ResultUtil;
import cn.yourdad.util.uuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.controller
 * @description:  普通用户注册controller
 * @author: wzj
 * @create: 2020-09-23 22:20
 **/

@RestController
public class RegistController {

    @Autowired
    private UserService userService;

    @RequestMapping("/regist")
    public Result regist(@RequestParam("uname") String uname, @RequestParam("sex") Integer sex,
                         @RequestParam("password") String password, @RequestParam("phoneNumber") Long phoneNumber){

        String uid = uuidUtil.getUUID(12);
        Long registTs = System.currentTimeMillis();

        User user = new User();
        user.setUid(uid);
        user.setUname(uname);
        user.setSex(sex);
        user.setRegistTs(registTs);
        user.setPassword(MD5Util.getMD5(password));
        user.setPhoneNumber(phoneNumber);


        boolean result = userService.insertUser(user);

        if (result){
            return ResultUtil.success("注册成功！");
        }

        return ResultUtil.error(400, "注册失败！");


    }

}
