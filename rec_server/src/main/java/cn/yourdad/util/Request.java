package cn.yourdad.util;

import cn.yourdad.pojo.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description:
 * @author: wzj
 * @create: 2020-09-29 19:44
 **/

public class Request {
	
	public static User getUserFromHttpServletRequest(HttpServletRequest request) {
		return (User) request.getSession().getAttribute("user");
	}

}
