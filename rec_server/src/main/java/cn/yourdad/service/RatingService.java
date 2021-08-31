package cn.yourdad.service;

import cn.yourdad.dao.RatingMapper;
import cn.yourdad.pojo.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.service
 * @description:
 * @author: wzj
 * @create: 2020-10-16 13:30
 **/
@Service
public class RatingService {

    @Autowired
    private RatingMapper ratingMapper;

    public Rating findRatingByUid(String uid){
       return ratingMapper.findRatingByUid(uid);
    }
}
