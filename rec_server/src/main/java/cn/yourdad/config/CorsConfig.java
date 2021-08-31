package cn.yourdad.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.config
 * @description: springboot vue跨域配置
 * @author: wzj
 * @create: 2020-09-23 18:17
 **/

/**
 * 没转vue先别用，不然thymeleaf引擎无法引用静态资源
 */
//@Configuration
//@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOrigins("*")
                //这里：是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //跨域允许时间
                .maxAge(3600);
    }
}