package cn.yourdad.utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Result, ResultScanner, Scan}
import org.apache.hadoop.hbase.util.Bytes

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.utils
 * @description: hbase连接助手
 * @author: wzj
 * @create: 2020-08-07 12:55
 **/
class HBaseConnHelper {

  def apply(): HBaseConnHelper = new HBaseConnHelper()

    def getConnection():Connection = {
      val config = HBaseConfiguration.create()
      config.addResource(
        new Path(ClassLoader.getSystemResource("config/core-site.xml").toURI)
      )
      config.addResource(
        new Path(ClassLoader.getSystemResource("config/hbase-site.xml").toURI)
      )

      val conn: Connection = ConnectionFactory.createConnection(config)

      conn
    }


    def scanTable(tableName:String) ={
      val config = HBaseConfiguration.create()
      config.addResource(
        new Path(ClassLoader.getSystemResource("config/core-site.xml").toURI)
      )
      config.addResource(
        new Path(ClassLoader.getSystemResource("config/hbase-site.xml").toURI)
      )

      val conn: Connection = ConnectionFactory.createConnection(config)
      val table = conn.getTable(TableName.valueOf(tableName))
      val scan = new Scan
      val rs: ResultScanner = table.getScanner(scan)
      val r = rs.iterator()
      while (r.hasNext){
        val row = r.next()
        println("rowkey =====" + Bytes.toString(row.getRow))
        println(Bytes.toString(row.getValue(Bytes.toBytes("song"), Bytes.toBytes("lysics"))))
      }

    }

}
