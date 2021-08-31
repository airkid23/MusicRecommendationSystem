package cn.yourdad.dao;

import cn.yourdad.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.dao
 * @description:
 * @author: wzj
 * @create: 2020-09-28 20:14
 **/


@Mapper
public interface UserMapper {

    /**
     * 插入用户
     * @param user
     * @return
     */
    @Insert("insert into user values(#{uid ,jdbcType=VARCHAR}, #{uname, jdbcType=VARCHAR}, #{sex, jdbcType=INTEGER}, " +
            "#{registTs, jdbcType=BIGINT}, #{password,jdbcType=VARCHAR}, #{phoneNumber, jdbcType=BIGINT} ) ")
    boolean insertUser(User user);

    /**
     * 通过用户名查询用户
     * @param uname
     * @param password
     * @return
     */
    @Select("select * from user where uname = #{uname} limit 1")
    User findUserByUname(String uname, String password);

    /**
     * 通过用户手机号码查询
     * @param phoneNumber
     * @param password
     * @return
     */
    @Select("select * from user where phoneNumber = #{phoneNumber} limit 1")
    User findUserByPhoneNumber(Long phoneNumber, String password);


    @Select("select * from user where uname  = #{name}")
    User checkUserNameIsExist(String name);

    @Select("select * from user where phoneNumber = #{phoneNumber}")
    User checkPhoneNumberIsExist(Long phoneNumber);

}
