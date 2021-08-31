package cn.yourdad.controller;

import cn.yourdad.dao.UserMapper;
import cn.yourdad.pojo.Result;
import cn.yourdad.pojo.User;
import cn.yourdad.service.UserService;
import cn.yourdad.util.MD5Util;
import cn.yourdad.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.controller
 * @description: login controller
 * @author: wzj
 * @create: 2020-09-23 18:15
 **/
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String login(@RequestParam("account") Object account, @RequestParam("password") String password,
                        HttpSession session, HttpServletRequest request){

        String passwd = MD5Util.getMD5(password);
        boolean type = checkAccountType(account);
        if (!type){
            User user = userService.findUserByPhoneNumber(Long.valueOf(account.toString()), passwd);
            if (user.getPhoneNumber() != null){
                session.setAttribute("user", user);
                return "redirect:/index";
            }else{
                return "redirect:/toLogin";
            }
        }else{
            User user = userService.findUserByUname(account.toString(), passwd);
            if (user.getPhoneNumber() != null){
                session.setAttribute("user", user);
                return "redirect:/index";
            }else{
                return "redirect:/toLogin";
            }
        }

    }

    @ResponseBody
    @RequestMapping("/checkUname")
    public Result checkUserNameIsExist(@RequestParam("name") String name){

       User user = userService.checkUserNameIsExist(name);
       if (user == null){
           return ResultUtil.success("可以使用此用户名");
       }

       return ResultUtil.error(412, "已存在此用户名");
    }

    @ResponseBody
    @RequestMapping("/checkPhoneNumber")
    public Result checkPhoneNumberIsExist(@RequestParam("phoneNumber") Long phoneNumber){

        User user = userService.checkPhoneNumberIsExist(phoneNumber);
        if (user == null){
            return ResultUtil.success("可使用此手机号码注册");
        }

        return ResultUtil.error(413, "手机号码已注册，<a href=\"/toLogin\">去登陆？</a>");

    }

    /**
     * false 手机号码 true 用户名
     * @param account
     * @return
     */
    private boolean checkAccountType(Object account) {

        if (account.toString().length() > 8){
            return false;
        }
        return true;
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/toLogin";
    }
    }
