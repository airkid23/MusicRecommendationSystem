package cn.yourdad.service;

import cn.yourdad.dao.UserMapper;
import cn.yourdad.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.service
 * @description:
 * @author: wzj
 * @create: 2020-09-28 20:14
 **/

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public boolean insertUser(User user){

        boolean result = userMapper.insertUser(user);
        return result;

    }

    public User findUserByUname(String uname, String password){

        User user = userMapper.findUserByUname(uname, password);
        return user;

    }

    public User findUserByPhoneNumber(Long phoneNumber, String password){

        User user = userMapper.findUserByPhoneNumber(phoneNumber, password);
        return user;

    }

    public User checkUserNameIsExist(String name){

        return userMapper.checkUserNameIsExist(name);
    }

    public User checkPhoneNumberIsExist(Long phoneNumber){

        return userMapper.checkPhoneNumberIsExist(phoneNumber);
    }


}
