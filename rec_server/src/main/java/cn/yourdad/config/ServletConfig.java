package cn.yourdad.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import java.util.Collections;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.config
 * @description:  解决Sessionid
 * @author: wzj
 * @create: 2020-10-02 11:58
 **/

@Configuration
public class ServletConfig {


    @Bean
    public ServletContextInitializer servletContextInitializer1() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE) );

            }

        };
    }

}
