package cn.yourdad.offlineLFM

import breeze.numerics.sqrt
import cn.yourdad.mpping.{PlaylistID_Mapping, RatingTransfrom, UID_Mapping}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.offlineLFM
 * @description: 最小二乘法训练LFM参数
 * @author: wzj
 * @create: 2020-10-11 11:07
 * */
object ALSTrainer {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getSimpleName)
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._
    val playlistID_Mapping = new PlaylistID_Mapping
    playlistID_Mapping.trans()
    val uid_mapping = new UID_Mapping
    uid_mapping.trans()
    println("trans mapping over")
    val ratingTransfrom = new RatingTransfrom
    val ratingRDD = ratingTransfrom.getTransRatingRDD()


    // 随机切分数据集，生成训练集和测试集
    val splits = ratingRDD.randomSplit(Array(0.8, 0.2))
    val trainingRDD = splits(0)
    val testRDD = splits(1)

    // 模型参数选择，输出最优参数
    adjustALSParam(trainingRDD, testRDD)

    spark.close()
  }

  /**
   * 获取较优的ALS参数
   * @param trainData 训练集
   * @param testData  测试集
   */
  // (150,0.5,1.398812553972984)  (200,0.5,1.4624028278001513)
  def adjustALSParam(trainData: RDD[Rating], testData: RDD[Rating]): Unit ={
    //从以下参数循环测试
    val result = for( rank <- Array(30, 50, 100, 150, 200, 250, 300, 350, 400, 500); lambda <- Array( 0.01, 0.05, 0.1,
      0.2, 0.3, 0.5, 0.7, 0.8, 1 ))
      yield {
        val model = ALS.train(trainData, rank, 10, lambda)
        // 计算当前参数对应模型的rmse，返回Double
        val rmse = getRMSE( model, testData )
        ( rank, lambda, rmse )
      }
    // 控制台打印输出最优参数
    println(result.minBy(_._3))
    println("模型训练完毕")
  }

  /**
   * 计算测试数据与模型预测的均方根误差
   * @param model 模型
   * @param data 测试数据
   * @return
   */
  def getRMSE(model: MatrixFactorizationModel, data: RDD[Rating]): Double = {
    // 计算预测评分
    val userProducts = data.map(item => (item.user, item.product))
    val predictRating = model.predict(userProducts)

    // 以uid，mid作为外键，inner join实际观测值和预测值
    val observed = data.map( item => ( (item.user, item.product), item.rating ) )
    val predict = predictRating.map( item => ( (item.user, item.product), item.rating ) )
    // 内连接得到(uid, mid),(actual, predict)
    sqrt(
      observed.join(predict).map{
        case ( (uid, mid), (actual, pre) ) =>
          val err = actual - pre
          err * err
      }.mean()
    )
  }
}