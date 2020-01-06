import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.SparkConf
import org.apache.spark.ml.feature.{RegexTokenizer,StopWordsRemover,
  StringIndexer,CountVectorizer,
  CountVectorizerModel,VectorAssembler,
  IDF,OneHotEncoderEstimator}

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}

// import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
// import org.apache.spark.ml.evaluation
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator

import org.apache.spark.ml.param.ParamMap

object Trainer {
  def main(args: Array[String]): Unit = {
    // Des réglages optionnels du job spark. Les réglages par défaut fonctionnent très bien pour ce TP.
    // On vous donne un exemple de setting quand même
    val conf = new SparkConf().setAll(Map(
      "spark.scheduler.mode" -> "FIFO",
      "spark.speculation" -> "false",
      "spark.reducer.maxSizeInFlight" -> "48m",
      "spark.serializer" -> "org.apache.spark.serializer.KryoSerializer",
      "spark.kryoserializer.buffer.max" -> "1g",
      "spark.shuffle.file.buffer" -> "32k",
      "spark.default.parallelism" -> "12",
      "spark.sql.shuffle.partitions" -> "12"
    ))

    // Initialisation du SparkSession qui est le point d'entrée vers Spark SQL (donne accès aux dataframes, aux RDD,
    // création de tables temporaires, etc., et donc aux mécanismes de distribution des calculs)

    val spark = SparkSession
      .builder
      .config(conf)
      .appName("TP Twitter : Trainer")
      .getOrCreate()

    import spark.implicits._

    println("Hello World")

    val df:DataFrame = spark
      .read
      .option("header", true)
      .option("separator",",")
      .option("inferSchema", "true")
      .parquet("data/data.parquet")

    val cdf:DataFrame = df
        .withColumn("text",lower($"text"))
        .withColumn("text",regexp_replace($"text","#nohaygolpeenbolivia.?",""))
        .withColumn("text",regexp_replace($"text","#golpedeestadobolivia.?",""))
        .drop("__index_level_0__")
        .drop("words")
        .drop("test")

    val tokenizer = new RegexTokenizer()
      .setPattern("\\W+")
      .setGaps(true)
      .setInputCol("text")
      .setOutputCol("tokens")
    // val dfTokenized = tokenizer.transform(cdf)

    val remover = new StopWordsRemover("spanish")
      .setInputCol(tokenizer.getOutputCol)
      .setOutputCol("filtered")


    // val dfsw = remover.transform(dfTokenized)

    val cvModel: CountVectorizer = new CountVectorizer()
      .setInputCol(remover.getOutputCol)
      .setOutputCol("vect")
      .setMinDF(50)

    // val dfv = cvModel.fit(dfsw).transform(dfsw)

    val idf = new IDF()
      .setInputCol(cvModel.getOutputCol)
      .setOutputCol("tfidf")

    // val dfidf:DataFrame = idf.fit(dfv).transform(dfv)

    val assembler = new VectorAssembler()
      .setInputCols(Array("tfidf"))
      .setOutputCol("features")

    // val df_f = assembler.transform(dfidf).drop("text","words","tokens","vect","tfidf","filtered")

    val lr = new LogisticRegression()
      .setElasticNetParam(0.0)
      .setFitIntercept(true)
      .setFeaturesCol("features")
      .setLabelCol("label")
      .setStandardization(true)
      .setPredictionCol("predictions")
      .setRawPredictionCol("raw_predictions")
      .setThresholds(Array(0.7, 0.3))
      .setTol(1.0e-6)
      .setMaxIter(20)

    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, remover,cvModel,idf, assembler,lr ))

    val model = pipeline.fit(cdf)

    val Array(train,test) = cdf.randomSplit(Array[Double](0.8, 0.2))
//    val size = (train.count,test.count)

    val predictions = model.transform(test)
//    predictions.select("label","predictions","probability").show(100)

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("predictions")
      .setMetricName("f1")

    val f1 = evaluator.evaluate(predictions)


    val evaluatorAUC = new BinaryClassificationEvaluator()
      .setLabelCol("label")
      .setRawPredictionCol("raw_predictions")
      .setMetricName("areaUnderROC")

    val AUC = evaluatorAUC.evaluate(predictions)


    println("Test set accuracy = " + f1)
    println("Test set AUC = " + AUC)

//    predictions.select("text","label","predictions").show(10,false)
  }

}
