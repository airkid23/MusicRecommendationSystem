package cn.yourdad.config;

import cn.yourdad.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.config
 * @description:  登录拦截器配置
 * @author: wzj
 * @create: 2020-10-06 18:32
 **/

@Configuration
public class LoginConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration registration = registry.addInterceptor(new LoginInterceptor());
        registration.addPathPatterns("/**");
        registration.excludePathPatterns("/toLogin", "/**/*.js", "/**/*.css", "/**/*.jpg", "/**/*.png", "/login",
                "/regist", "/toRegister", "/checkUname", "/checkPhoneNumber");
    }
}
