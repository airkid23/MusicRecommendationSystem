package cn.yourdad.util;

import java.util.UUID;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description:  获取uuid
 * @author: wzj
 * @create: 2020-09-28 19:44
 **/

public class uuidUtil {

    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");;
        return uuid;
    }

    public static String getUUID(Integer length){
        String uuid = getUUID().substring(0, length);
        return uuid;
    }
}
