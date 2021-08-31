package cn.yourdad.interceptor;

import cn.yourdad.pojo.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.interceptor
 * @description: 登录拦截器
 * @author: wzj
 * @create: 2020-10-06 18:28
 **/

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        User user = (User) request.getSession().getAttribute("user");
        if (user != null){
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/toLogin");

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
