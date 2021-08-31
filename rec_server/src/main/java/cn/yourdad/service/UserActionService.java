package cn.yourdad.service;

import cn.yourdad.dao.UserActionMapper;
import cn.yourdad.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.service
 * @description:
 * @author: wzj
 * @create: 2020-10-06 13:04
 **/

@Service
public class UserActionService {

    @Autowired
    private UserActionMapper userActionMapper;

    @Autowired
    private Jedis jedis;

    public void insertPlaylistScore2Rating(String uid, String playlistID, Double rating){

        Long timestamp = System.currentTimeMillis();
        insertRatingToRedis(uid, playlistID, rating);
        try{
            userActionMapper.insertPlaylistScore2Rating(uid, playlistID, rating, String.valueOf(timestamp));
        }catch (Exception e){
            updatePlaylistScore2Rating(uid, playlistID, rating, String.valueOf(timestamp));
        }


    }

    @Deprecated
    public boolean checkActionInRating(String uid, String playlistID, Double rating){

        return userActionMapper.checkActionInRating(uid, playlistID, rating);
    }

    public void insertRatingToRedis(String uid, String pid, Double rating){
        if (jedis.exists("uid:" + uid) && jedis.llen("uid:" + uid) >= Constant.REDIS_PLAYLIST_RATING_QUEUE_SIZE) {
            jedis.rpop("uid:" + uid);
        }
        jedis.lpush("uid:" + uid, pid + ":" + rating);
    }

    public void updatePlaylistScore2Rating(String uid, String playlistID, Double rating, String timestamp){

        userActionMapper.updatePlaylistScore2Rating(uid, playlistID, rating, timestamp);
    }
}
