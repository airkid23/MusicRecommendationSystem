package cn.yourdad.service;

import cn.yourdad.pojo.SingerInfoBean;
import cn.yourdad.util.HBaseConnUtil;
import com.alibaba.fastjson.JSON;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.service
 * @description:
 * @author: wzj
 * @create: 2020-10-09 17:42
 **/
@Service
public class SingerService {


    /**
     * 根据歌手id，从hbase提取歌手信息
     * @param singerID
     * @return
     */
    public SingerInfoBean getSingerInfoByID (String singerID){

        String tableName = "playlist";
        String family = "singer";
        String singerNameQulifier = "singerName";
        String singerImgQulifier = "singerImg";
        String singerName;
        String singerImg;
        SingerInfoBean singer = null;
        Connection connection = HBaseConnUtil.getConnection();

        try{
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(singerID));
            Result result = table.get(get);
            singerName = Bytes.toString(result.getValue(Bytes.toBytes(family), Bytes.toBytes(singerNameQulifier)));
            singerImg = Bytes.toString(result.getValue(Bytes.toBytes(family), Bytes.toBytes(singerImgQulifier)));
            if (singerName == null){
                return new SingerInfoBean(singerID, "歌手信息正在收录中", "null");
            }
            singer = new SingerInfoBean(singerID, singerName, singerImg);
        }catch (Exception e){
            System.out.println("hbase提取歌手名字出错");
        }
        return singer;
    }
}
