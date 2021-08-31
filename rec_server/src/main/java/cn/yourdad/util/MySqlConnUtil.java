package cn.yourdad.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description: get mysql connection
 * @author: wzj
 * @create: 2020-09-23 21:02
 **/

public class MySqlConnUtil {

    public static Connection getMysqlConnection(){

        String DRIVER = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/musicRecs?characterEncoding=utf8&useUnicode=true&useSSL=false";
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(url,"root","root");
        }catch (Exception e){
            System.out.println(e);
        }

        return conn;
    }
}
