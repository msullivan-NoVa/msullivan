package com.sullivan.mrs;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryParquetFile extends Configured implements Tool, Closeable {

    protected static final Logger LOGGER = LogManager.getLogger(WriteCsvToParquet.class);

    public static final String OUTPUT_DATA_ROOT = "C:/spark/output_data/";
    private static final String PARQUET_DATA_FILE = OUTPUT_DATA_ROOT +"timeseriesdata.parquet";


    // configuration keys
    public static final String INPUT_PATH = "spark.input.path";
    public static final String OUTPUT_PATH = "spark.output.path";
    public static final String IS_RUN_LOCALLY = "spark.is.run.local";
    public static final String DEFAULT_FS = "spark.default.fs";
    public static final String NUM_PARTITIONS = "spark.num.partitions";
    private static final String NEW_LINE_DELIMETER = "\n";
    public static final String SPARK_SQL_WAREHOUSE_DIR = "spark.sql.warehouse.dir";

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new QueryParquetFile(), args);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public int run(String[] strings) throws Exception {
        SparkSession spark = SparkSession
                .builder()
                .appName("QueryParquetFile")
                .config(INPUT_PATH, "file:///C:/spark/output_data")
                .config(OUTPUT_PATH, "file:///C:/spark/output_data")
                .config(DEFAULT_FS,true)
                .master("local[*]")
                .config(SPARK_SQL_WAREHOUSE_DIR, "file:///C:/spark/temp") // Necessary to work around a Windows bug in Spark 2.0.0; omit if you're not on Windows
                .getOrCreate();

        long startTime = System.currentTimeMillis();
        Dataset<Row> parquetFileDF = spark.read().parquet(PARQUET_DATA_FILE);

//        Parquet files can also be used to create a temporary view and then used in SQL statements
        parquetFileDF.createOrReplaceTempView("parquetFile");

//        if want view to be available to all sessions until spark application dies
//        parquetFileDF.createOrReplaceGlobalTempView();

        // earliest = 1_590_969_600 for 30 1590969600
        // latest =   1_593_561_599 for 30 1593561599 delta = 2591999
        //Dataset<Row> namesDF = spark.sql("SELECT Extract(DAY from from_unixtime(time,'yyyy-MM-dd HH:mm:ss.SSS')),sensor_1 FROM parquetFile where time between 1590969600 and 1593561599 ");// WHERE age BETWEEN 13 AND 19")
        Dataset<Row> namesDF = spark.sql("SELECT Extract(DAY from from_unixtime(time,'yyyy-MM-dd HH:mm:ss.SSS')) AS time1,min(sensor_1),max(sensor_1),avg(sensor_1) " +
                "FROM parquetFile where time between 1590969600 and 1593561599 GROUP BY time1 order by time1 asc");// WHERE age BETWEEN 13 AND 19")
        namesDF.show();
        //namesDF.groupBy("time").agg(mean("sensor_1"))
//        Encoder<PlottablePoints> encoder = Encoders.bean(PlottablePoints.class);
//        Dataset<PlottablePoints> namesDF = spark.sql("SELECT time,sensor_1 FROM parquetFile where time between 1588699200000 and 1589044799900 ").as(encoder);// WHERE age BETWEEN 13 AND 19")
//        namesDF.show();

//        List<PlottablePoints> plist = namesDF.collectAsList();
//        LOGGER.debug(String.format("list size = %d",plist.size()));
//        Iterator<PlottablePoints> iterator = plist.iterator();
//        int i=20;
//        while (i>0 && iterator.hasNext()) {
//            --i;
//            LOGGER.debug(iterator.next().toString());
//        }

        long elapsed = (System.currentTimeMillis() - startTime) / (1000);
        LOGGER.debug("Query and display took "+elapsed + " seconds for "+ namesDF.count() + " rows");

        spark.stop();
        return 0;
    }

}
