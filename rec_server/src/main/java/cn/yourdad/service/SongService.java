package cn.yourdad.service;

import cn.yourdad.dao.PlaylistMapper;
import cn.yourdad.dao.SongMapper;
import cn.yourdad.pojo.PlaylistInfoBean;
import cn.yourdad.pojo.Song;
import cn.yourdad.util.HBaseConnUtil;
import com.alibaba.fastjson.JSON;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.service
 * @description: 歌曲service
 * @author: wzj
 * @create: 2020-10-03 13:15
 **/

@Service
public class SongService {

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private PlaylistMapper playlistMapper;

    public List<Song> getSongs(List<String> ids){

        return songMapper.getSongs(ids);
    }

    public Song getSongByID(String songID){

        return songMapper.getSongByID(songID);
    }


    /**
     * 从hbase获取歌词
     * @param songID
     * @return
     * @throws IOException
     */
    public String getSongLysics(String songID){

        String tableName = "playlist";
        String family = "song";
        String qulifier = "lysics";
        byte[] lysics = null;
        Connection connection = HBaseConnUtil.getConnection();

        try{
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(songID));
            Result result = table.get(get);
            lysics = result.getValue(Bytes.toBytes(family), Bytes.toBytes(qulifier));

        }catch (Exception e){
            e.printStackTrace();
        }
        if (lysics == null)
            return "此歌暂无歌词";

        return Bytes.toString(lysics).replaceAll("\\\\n", "<br>");
    }

    /**
     * 获取歌曲存在歌单倒排表
     * @param songID
     * @return
     */
    public List<PlaylistInfoBean> getInvertedIndexPlaylists(String songID){
        String tableName = "playlist";
        String family = "song";
        String qulifier = "InvertedIndex";
        byte[] invertedIndex = null;
        List<String> list = new ArrayList<>();
        List<PlaylistInfoBean> playlists = new ArrayList<>();
        Connection connection = HBaseConnUtil.getConnection();

        try{
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(songID));
            Result result = table.get(get);
            invertedIndex = result.getValue(Bytes.toBytes(family), Bytes.toBytes(qulifier));

        }catch (Exception e){
            e.printStackTrace();
        }
        String[] strs = Bytes.toString(invertedIndex).split("\\|");
        for (String str: strs){
            list.add(str);
        }
        playlists = playlistMapper.getPlaylistInfoByList(list);
        if (playlists.size() > 5){
            return playlists.subList(0, 5);
        }
        return playlists;
    }

}
