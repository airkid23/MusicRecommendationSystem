package cn.yourdad.dataGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import cn.yourdad.pojo.Rating;
import cn.yourdad.util.MySqlConnUtil;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.dataGenerator
 * @description: 生成rating数据
 * @author: wzj
 * @create: 2020-10-10 17:36
 **/

public class fakeRatingDataProducer {


    public static List<String> getRandomPlaylistID() throws SQLException {

        Connection connection = MySqlConnUtil.getMysqlConnection();
        List<String> list = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT playlistID, playCount FROM playlist ORDER BY RAND() LIMIT 1";

        Statement statement = connection.createStatement();
        rs= statement.executeQuery(sql);
        rs.next();
        String playlist = rs.getString("playlistID");
        list.add(playlist);
        list.add(rs.getString("playCount"));
        statement.close();
        connection.close();

        return list;
    }

    public static String getRandomUid() throws SQLException {

        Connection connection = MySqlConnUtil.getMysqlConnection();
        ResultSet rs = null;
        String sql = "SELECT uid FROM user ORDER BY RAND() LIMIT 1";

        Statement statement = connection.createStatement();
        rs= statement.executeQuery(sql);
        rs.next();
        String uid = rs.getString("uid");
        statement.close();
        connection.close();

        return uid;

    }

    public static double getRating(Long playCount){

        double rating = 0.0;
        if (playCount > 5000000){
            do{
                rating = new Random().nextInt(6);
            }while (rating < 1.0);
        }else {
            do{
                rating = new Random().nextInt(6);
            }while (rating == 0.0);
        }

        return rating;
    }

    public static void insertRating(Rating rating) throws SQLException {

        try{
            Connection connection = MySqlConnUtil.getMysqlConnection();
            String sql = "insert into rating values(?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, rating.getUid());
            preparedStatement.setString(2, rating.getPlaylistID());
            preparedStatement.setDouble(3, rating.getRate());
            preparedStatement.setString(4, rating.getRate_time());
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        }catch (Exception e){
            System.out.println("已存在主键");
        }




    }


    public static void main(String[] args) throws SQLException {

        String uid;
        String playlistID;
        Long playCount ;
        double rating ;
        for (int i = 0; i < 256; i++) {
            uid = getRandomUid();
            List<String> list = new ArrayList<>();
            list = getRandomPlaylistID();
            playlistID = list.get(0);
            playCount = Long.parseLong(list.get(1));
            rating = getRating(playCount);
//            System.out.println(uid + "\t" + playlistID + "\t" + rating + "\t" + playCount);
        insertRating(new Rating(uid, playlistID, rating, String.valueOf(System.currentTimeMillis())));

        }



    }
}
