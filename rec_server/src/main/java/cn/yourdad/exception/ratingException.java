package cn.yourdad.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.exception
 * @description:
 * @author: wzj
 * @create: 2020-10-06 14:43
 **/

//@ControllerAdvice
public class ratingException {

    public String nologinRatingHadle(){

        return "redirect:/toLogin";
    }
}
