package cn.yourdad.service;

import cn.yourdad.dao.PlaylistMapper;
import cn.yourdad.pojo.ItemCFRecommendation;
import cn.yourdad.pojo.PlaylistInfoBean;
import cn.yourdad.util.HBaseConnUtil;
import org.apache.avro.data.Json;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.service
 * @description:  service 获取playlist
 * @author: wzj
 * @create: 2020-09-25 13:41
 **/

@Service
public class PlaylistService {

    @Autowired
    private PlaylistMapper playlistMapper;


    /**
     * 获取所有歌单
     * @return
     */
    public List<PlaylistInfoBean> getPlaylist(){

        List<PlaylistInfoBean> playlistInfoBeans = playlistMapper.getPlaylist();
        return playlistInfoBeans;
    }

    /**
     * 获取最多播放歌单
     * @return
     */
    public  List<String> getPlMostPlay(){
        List<String> playlistInfoBeans = playlistMapper.getPlMostPlay();
        return playlistInfoBeans;
    }

    /**
     * 根据歌单id获取歌单所有歌曲id
     * @param playlistID
     * @return
     */
    public  List<String>  getSongsID(String playlistID){

        String songsID = playlistMapper.getSongsID(playlistID);
        String[] arrays = songsID.split(",");
        List<String> ids = new ArrayList<>();
        for (String arr: arrays
             ) {
            ids.add(arr.trim());
        }
        return ids;
    }

    /**
     * 根据id查询歌单名
     * @param playlistID
     * @return
     */
    public String getPlaylistName(String playlistID){

        String playlistName = playlistMapper.getPlaylistName(playlistID);
        return playlistName;
    }

    /**
     * 根据id获取歌单信息
     * @param playlistID
     * @return
     */
    public PlaylistInfoBean getPlaylistByID(String playlistID){

        PlaylistInfoBean playlist = playlistMapper.getPlaylistByID(playlistID);
        return playlist;
    }

    /**
     * 从hbase 获取歌单的相似歌单id
     * @param songID
     * @return          返回的是hbase中的recPl的一行记录
     */
    public String getSimPlaylistIDs(String playlistID){

        String tableName = "playlist";
        String family = "recs";
        String qulifier = "recPl";
        byte[] recList = null;
        Connection connection = HBaseConnUtil.getConnection();

        try{
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(playlistID));
            Result result = table.get(get);
            recList = result.getValue(Bytes.toBytes(family), Bytes.toBytes(qulifier));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("获取推荐列表出错");
        }
        String str =Bytes.toString(recList);
        recList = null;
        return str;
    }

    /**
     * 正则表达式匹配推荐歌单id
     * @param recList
     * @return
     */
    public List<String> transSongIDs(String recList){

        List<String> recIDs = new ArrayList<>();
        String pattern = "Recommendation\\(.*?\\)";
        try{
            Pattern r = Pattern.compile(pattern);
            Matcher matcher = r.matcher(recList);
            while (matcher.find()){
                recIDs.add(matcher.group(0).replace("Recommendation",""));
            }
        }catch (Exception e){
            System.out.println("正则匹配出错");
            return null;
        }
        return recIDs;
    }

    /**
     * 获取推荐结果,将(5214463956,1.0)转换成(plbean, simscore)
     * @param playlistID
     * @return
     */
    public List<PlaylistInfoBean> getSimPlaylist(String playlistID, Integer num)throws Exception{

        List<String> simPlaylist = transSongIDs(getSimPlaylistIDs(playlistID));
        if (simPlaylist == null){
            return null;
        }
        List<String> ids = new ArrayList<>();

        for (String id: simPlaylist
        ) {
            ids.add(id.split(",")[0].substring(1));
        }
        List<PlaylistInfoBean> playlists = playlistMapper.getPlaylistInfoByList(ids);

        if (playlists.size() < 5){
            return playlists;
        }else{
            return playlists.subList(0, num);
        }
    }

    /**
     * 根据pid获取相似度
     * @param playlistID pid
     * @param num        返回推荐结果个数
     * @return
     */
    public List<Double> getPlaylistSimScoreByPID(String playlistID, Integer num){


        List<String> simPlaylist = transSongIDs(getSimPlaylistIDs(playlistID));
        if (simPlaylist == null){
            return null;
        }
        List<Double> simScore = new ArrayList<>();
        for (String id: simPlaylist
        ) {
            simScore.add(Double.parseDouble(id.split(",")[1].replaceAll("\\)","")));
        }

        if (simScore.size() < 5){
            return simScore;
        }else {
            return simScore.subList(0, num);
        }
    }

    /**
     * 将u-p-r评分类型插入hbase
     * @param uid
     * @param playlistID
     * @param rating
     */
    public void insertPlaylistRating2Hbase(String uid, String playlistID, int rating){

        String tableName = "playlist";
        String family = "action";
        String qulifier = "rating";
        Connection connection = HBaseConnUtil.getConnection();

        try{
            Table table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(uid + "-" + playlistID));
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qulifier), Bytes.toBytes(rating));
            table.put(put);
        }catch (Exception e){
            System.out.println("插入用户行为记录出错");
        }
    }


    public List<PlaylistInfoBean> getPlaylistInfoByList( List<String> ids){
        return  playlistMapper.getPlaylistInfoByList(ids);

    }

    public String getCreatorNameByID(String creatorID){
        return playlistMapper.getCreatorNameByID(creatorID);
    }


    /**
     * 根据用户id获取userCF推荐
     * @param uid
     * @return
     */
    public List<PlaylistInfoBean> getUserCFRecs(String uid) {

        List<PlaylistInfoBean> playlists = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String TABLENAME = "playlist";
        String FAMILY = "recs";
        String QULIFIER = "userCFRec";
        String recString = null;
        Connection connection = HBaseConnUtil.getConnection();

        try{
            Table table = connection.getTable(TableName.valueOf(TABLENAME));
            Get get = new Get(Bytes.toBytes(uid));
            Result result = table.get(get);
            recString = Bytes.toString(result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QULIFIER)));
            String[] arrays = recString.split("\\|");
            for (String arr: arrays
                 ) {
                list.add(arr);
            }
            playlists = playlistMapper.getPlaylistInfoByList(list);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("获取userCF推荐列表出错");
        }
        return playlists;
    }

    public List<PlaylistInfoBean> getLFMRecs(String uid) {

        List<PlaylistInfoBean> playlists = new ArrayList<>();
        List<String> list = new ArrayList<>();
        String TABLENAME = "playlist";
        String FAMILY = "recs";
        String QULIFIER = "LFMRec";
        String recString = null;
        Connection connection = HBaseConnUtil.getConnection();

        try{
            Table table = connection.getTable(TableName.valueOf(TABLENAME));
            Get get = new Get(Bytes.toBytes(uid));
            Result result = table.get(get);
            recString = Bytes.toString(result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QULIFIER)));
            String[] arrays = recString.split("\\|");
            for (String arr: arrays
            ) {
                list.add(arr);
            }
            playlists = playlistMapper.getPlaylistInfoByList(list);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("获取LFMRec推荐列表出错");
        }
        return playlists;
    }

    public ItemCFRecommendation getItemCFRecByUID(String uid) {
        List<String> list = new ArrayList<>();
        String TABLENAME = "playlist";
        String FAMILY = "recs";
        String QULIFIER = "itemCFRec";
        String recString = null;
        ItemCFRecommendation itemCFRecommendation = null;
        Connection connection = HBaseConnUtil.getConnection();
        try{
            Table table = connection.getTable(TableName.valueOf(TABLENAME));
            Get get = new Get(Bytes.toBytes(uid));
            Result result = table.get(get);
            recString = Bytes.toString(result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QULIFIER)));
            String[] pl = recString.split("~")[0].split(",");
            String pled = pl[0].replace("(", "");
            Double pledScore = Double.parseDouble(pl[1].replace(")", ""));
            String[] playlistIDs = recString.split("~")[1].split("\\|");
            for (String s: playlistIDs){
                list.add(s);
            }
            List<PlaylistInfoBean> playlists = new ArrayList<>();
            playlists = playlistMapper.getPlaylistInfoByList(list);
            itemCFRecommendation = new ItemCFRecommendation(pled, pledScore, playlists);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("获取itemCFRec推荐列表出错");
        }

        return itemCFRecommendation;
    }


}
