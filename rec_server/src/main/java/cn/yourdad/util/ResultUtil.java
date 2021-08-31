package cn.yourdad.util;

import cn.yourdad.pojo.Result;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description: 避免result冗余情况，应该增加工具类，
 * 封装请求失败和成功时候的方法，这里使用静态方法
 * @author: wzj
 * @create: 2020-09-23 18:58
 **/

public class ResultUtil {
    public static Result success(Object object) {
        Result result = new Result();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(object);
        return result;
    }

    public static Result success() {
        return success(null);
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}