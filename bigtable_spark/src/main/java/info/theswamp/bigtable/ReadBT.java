package info.theswamp.bigtable;


import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog;

import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
public class ReadBT {


    // just reads rows from Bigtable and prints
    public static void main(String[] args) {


        String catalog = "{\"table\": {\"namespace\": \"default\", \"name\": \"walmart\", \"tableCoder\":\"PrimitiveType\" }," +
                "\"rowkey\": \"key\"," +
                "\"columns\": {" +
                "\"keyx\": {\"cf\":\"rowkey\", \"col\":\"key\", \"type\":\"string\"}," +
                "\"weekly_sales\": {\"cf\":\"store_sales\", \"col\":\"weekly_sales\", \"type\":\"string\"} } }";

        SparkSession spark = SparkSession
                .builder()
                .appName("JavaTC")
                .getOrCreate();

        Map<String, String> options = new HashMap<>();
        options.put( HBaseTableCatalog.tableCatalog(), catalog);

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
        SQLContext sqlContext = new SQLContext(jsc);
        Dataset df =  sqlContext.read().options(options)
                .format("org.apache.spark.sql.execution.datasources.hbase")
                .load();


        String unixTimestamp = String.valueOf(Instant.now().getEpochSecond());
        String filename = "gs://zdenko-springml/results_" + unixTimestamp;  // path needs to be unique

        df.printSchema();

        df.show();
        spark.stop();

    }
}
