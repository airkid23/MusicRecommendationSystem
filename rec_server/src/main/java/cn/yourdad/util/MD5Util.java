package cn.yourdad.util;

import org.springframework.util.DigestUtils;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description:  MD5
 * @author: wzj
 * @create: 2020-09-25 13:43
 **/

public class MD5Util {
    private static final String slat = "&%5123***&&%%$$#@";

    public static String getMD5(String str) {
        String base = str +"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

}
