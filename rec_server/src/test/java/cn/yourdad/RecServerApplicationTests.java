package cn.yourdad;

import cn.yourdad.dao.PlaylistMapper;
import cn.yourdad.pojo.PlaylistInfoBean;
import cn.yourdad.pojo.SingerInfoBean;
import cn.yourdad.pojo.User;
import cn.yourdad.service.PlaylistService;
import cn.yourdad.service.UserService;
import cn.yourdad.util.HBaseConnUtil;
import cn.yourdad.util.MD5Util;
import cn.yourdad.util.MySqlConnUtil;
import cn.yourdad.util.uuidUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.bytebuddy.asm.Advice;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class RecServerApplicationTests {

	@Autowired
	PlaylistService playlistService;

	@Autowired
	UserService userService;

	@Autowired
	PlaylistMapper playlistMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void test(){
		MySqlConnUtil mySqlConnUtil = new MySqlConnUtil();
		Connection conn = mySqlConnUtil.getMysqlConnection();
		System.out.println(conn);
	}

	@Test
	void getPlaylistData(){

		List<PlaylistInfoBean> playlistInfoBeans = playlistService.getPlaylist();
		for (PlaylistInfoBean p: playlistInfoBeans
			 ) {
			System.out.println(p);
		}
	}


	@Test
	void getUUID(){
		System.out.println(uuidUtil.getUUID(5));

	}

	@Test
	void testInsert(){
		String uid = uuidUtil.getUUID(12);
		Long registTs = System.currentTimeMillis();

		User user = new User();
		user.setUid(uid);
		user.setUname("uname");
		user.setSex(0);
		user.setRegistTs(registTs);
		user.setPassword("password");
		user.setPhoneNumber(Long.valueOf("15307666626"));

		System.out.println(userService.insertUser(user));
	}

	@Test
	void md5Test(){
		System.out.println(MD5Util.getMD5("test"));
		System.out.println(MD5Util.getMD5("test"));
		System.out.println(MD5Util.getMD5("ok"));
		System.out.println(MD5Util.getMD5("test1"));

	}

	@Test
	void trans(){
		String str = "[00:00.000] ?????? : ??????\\n[00:01.000] ?????? : ??????\\n[00:05.290]??????????????????/?????????/????????????\\n[00:06.596]\\n[00:12.917]???????????????????????????????????????\\n[00:16.052]??????????????????????????????????????????\\n[00:19.396]???????????????????????????????????????\\n[00:22.452]??????????????????????????????????????????\\n[00:25.926]????????????????????????????????????\\n[00:28.800]????????????????????????????????????\\n[00:32.274]???";
		System.out.println(str.replaceAll("\\\\n", "<br>"));
	}

	@Test
	void getRecs() {


		String tableName = "playlist";
		String family = "recs";
		String qulifier = "recPl";
		byte[] recs = null;
		org.apache.hadoop.hbase.client.Connection connection = HBaseConnUtil.getConnection();

		try{
			Table table = connection.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes("97562281"));
			Result result = table.get(get);
			recs = result.getValue(Bytes.toBytes(family), Bytes.toBytes(qulifier));
		}catch (Exception e){

		}
		String result = Bytes.toString(recs);
		recs = null;
		System.out.println(result);
	}

	@Test
	void transRec(){

		String str = "List(Recommendation(2129489084,0.9), Recommendation(2134203011,0.9), Recommendation(2620713775,0.9), Recommendation(2756282456,0.9), Recommendation(2684061183,0.9), Recommendation(2238072247,0.9), Recommendation(564801446,0.84), Recommendation(755965175,0.84), Recommendation(2173169909,0.73), Recommendation(17051302,0.73), Recommendation(52342656,0.73), Recommendation(632021463,0.73), Recommendation(941890036,0.64))\n";
		String pattern = "Recommendation\\(.*?\\)";
		Pattern r = Pattern.compile(pattern);
		Matcher matcher = r.matcher(str);
		try{
			while (matcher.find()){
				System.out.println(matcher.group(0));

			}
		}catch (Exception e){
			System.out.println("haha");
		}

	}


	@Test
	void getSimPlaylist(){
//		String playlistID = "2172125110";
//		String simIDs = playlistService.getSimPlaylistIDs(playlistID);
//		System.out.println(simIDs);
//		System.out.println(playlistService.transSongIDs(simIDs));
////		List<PlaylistInfoBean> playlists = playlistService.getSimPlaylist(playlistID,4);
//		List<Double> simScore = playlistService.getPlaylistSimScoreByPID(playlistID,4);
//		System.out.println(playlists);
//		System.out.println(simScore);

	}

	@Test
	void plMostPlay(){
		List<PlaylistInfoBean> playlistInfoBeans = new ArrayList<>();
		List<String> playlistIDs = playlistService.getPlMostPlay();

		for (String playlistID: playlistIDs
		) {
			PlaylistInfoBean p = playlistService.getPlaylistByID(playlistID);
			playlistInfoBeans.add(p);
		}
		System.out.println(playlistInfoBeans);
	}


	@Test
	void testSinger(){
		String tableName = "playlist";
		String family = "singer";
		String singerNameQulifier = "singerName";
		String singerImgQulifier = "singerImg";
		String singerName = null;
		String singerImg = null;
		org.apache.hadoop.hbase.client.Connection connection = HBaseConnUtil.getConnection();

		try{
			Table table = connection.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes("2118"));
			Result result = table.get(get);
			singerName = Bytes.toString(result.getValue(Bytes.toBytes(family), Bytes.toBytes(singerNameQulifier)));
			singerImg = Bytes.toString(result.getValue(Bytes.toBytes(family), Bytes.toBytes(singerImgQulifier)));
			System.out.println(singerImg);
			System.out.println(singerName);
			SingerInfoBean singer = new SingerInfoBean("2118", singerName, singerImg);
			JSON j = (JSON) JSON.toJSON(singer);
			System.out.println(j);
		}catch (Exception e){
			System.out.println("hbase????????????????????????");
		}
	}


	@Test
	void testInvertIndex(){
		String songID = "1320074622";
		String tableName = "playlist";
		String family = "song";
		String qulifier = "InvertedIndex";
		byte[] invertedIndex = null;
		org.apache.hadoop.hbase.client.Connection connection = HBaseConnUtil.getConnection();

		try{
			Table table = connection.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(songID));
			Result result = table.get(get);
			invertedIndex = result.getValue(Bytes.toBytes(family), Bytes.toBytes(qulifier));

		}catch (Exception e){
			e.printStackTrace();
		}

		System.out.println(Bytes.toString(invertedIndex));
	}

	@Test
	void getItemCF(){
		playlistService.getItemCFRecByUID("23be84ee678d");
	}

	@Test
	void testRedis(){
		Jedis jedis = new Jedis("localhost");
		// ?????? Redis ???????????????????????????????????????????????????????????????
		// jedis.auth("123456");
		System.out.println("????????????");
		//????????????????????????
		System.out.println("??????????????????: "+jedis.ping());

		//????????????????????????
		jedis.lpush("site-list", "Runoob");
		jedis.lpush("site-list", "Google");
		jedis.lpush("site-list", "Taobao");
		// ??????????????????????????????
		List<String> list = jedis.lrange("site-list", 0 ,2);
		for(int i=0; i<list.size(); i++) {
			System.out.println("????????????: "+list.get(i));
		}

		Set<String> keys = jedis.keys("*");
		Iterator<String> it=keys.iterator() ;
		while(it.hasNext()){
			String key = it.next();
			System.out.println(key);
		}
	}
}
