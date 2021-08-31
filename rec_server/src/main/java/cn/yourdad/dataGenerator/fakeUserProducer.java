package cn.yourdad.dataGenerator;

import cn.yourdad.pojo.User;
import cn.yourdad.service.UserService;
import cn.yourdad.util.MD5Util;
import cn.yourdad.util.MySqlConnUtil;
import cn.yourdad.util.uuidUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static cn.yourdad.dataGenerator.NickName.generateName;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.dataGenerator
 * @description: 生成用户
 * @author: wzj
 * @create: 2020-10-10 16:09
 **/

public class fakeUserProducer {

    @Autowired
    private  UserService userService;


    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static int getNum(int start,int end) {
        return (int)(Math.random()*(end-start+1)+start);
    }

    private static String[] telFirst="134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
    private static String getTel() {
        int index=getNum(0,telFirst.length-1);
        String first=telFirst[index];
        String second=String.valueOf(getNum(1,888)+10000).substring(1);
        String third=String.valueOf(getNum(1,9100)+10000).substring(1);
        return first+second+third;
    }

    private static void insertUsers(User user) throws SQLException {

        Connection connection = MySqlConnUtil.getMysqlConnection();
        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO user(uid, uname, sex, registTs, password, phoneNumber) values(?,?,?,?,?,?)";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getUid());
        preparedStatement.setString(2, user.getUname());
        preparedStatement.setInt(3, user.getSex());
        preparedStatement.setLong(4, user.getRegistTs());
        preparedStatement.setString(5, user.getPassword());
        preparedStatement.setLong(6, user.getPhoneNumber());
        preparedStatement.execute();

        preparedStatement.close();
        connection.close();

    }
    public static void main(String[] args) throws SQLException {

        for (int i = 0; i < 167; i++) {
            String uid = uuidUtil.getUUID(12);
            Long registTs = System.currentTimeMillis();
            User user = new User();
            user.setUid(uid);
            user.setUname(generateName());
            user.setSex(new Random().nextInt(3));
            user.setRegistTs(registTs);
            user.setPassword(MD5Util.getMD5("password"));
            user.setPhoneNumber(Long.parseLong(getTel()));


            insertUsers(user);
            System.out.println(user.getUname() + "注册成功");
        }


//        String uid = uuidUtil.getUUID(12);
//        Long registTs = System.currentTimeMillis();
//        User user = new User();
//        user.setUid(uid);
//        user.setUname("admin");
//        user.setSex(1);
//        user.setRegistTs(registTs);
//        user.setPassword(MD5Util.getMD5("admin"));
//        user.setPhoneNumber(Long.parseLong("15307666626"));
//
//
//        insertUsers(user);



    }
}
