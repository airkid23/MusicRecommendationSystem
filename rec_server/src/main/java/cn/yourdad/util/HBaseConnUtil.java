package cn.yourdad.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.util
 * @description: hbase 连接助手
 * @author: wzj
 * @create: 2020-10-03 19:49
 **/

public class HBaseConnUtil {

    private static Connection connection;

    public static Connection getConnection(){
        if (connection == null){
            try{
                Configuration conf = HBaseConfiguration.create();
                conf.addResource(
                        new Path("/home/wzj/IdeaProjects/MusicRecommendationSystem/Recommender/DataLoader/src/main/resources/config/core-site.xml")
                );
                conf.addResource(
                        new Path("/home/wzj/IdeaProjects/MusicRecommendationSystem/Recommender/DataLoader/src/main/resources/config/hbase-site.xml")
                );
                connection = ConnectionFactory.createConnection(conf);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return connection;
    }

}
