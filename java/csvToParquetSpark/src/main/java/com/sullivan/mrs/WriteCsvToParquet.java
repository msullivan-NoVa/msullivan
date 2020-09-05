package com.sullivan.mrs;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.Closeable;
import java.io.IOException;

public class WriteCsvToParquet extends Configured implements Tool, Closeable {

    protected static final Logger LOGGER = LogManager.getLogger(WriteCsvToParquet.class);

    private static final String CSV_INPUT_FILE = "C:\\Users\\mathe\\data\\test_data_30_Days_10hz.csv";
    public static final String OUTPUT_DATA_ROOT = "C:/spark/output_data/";
    private static final String PARQUET_DATA_FILE = OUTPUT_DATA_ROOT +"timeseriesdata.parquet";

    // configuration keys
    public static final String INPUT_PATH = "spark.input.path";
    public static final String OUTPUT_PATH = "spark.output.path";
    public static final String IS_RUN_LOCALLY = "spark.is.run.local";
    public static final String DEFAULT_FS = "spark.default.fs";
    public static final String NUM_PARTITIONS = "spark.num.partitions";
    private static final String NEW_LINE_DELIMETER = "\n";

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new WriteCsvToParquet(), args);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public int run(String[] strings) throws Exception {
        SparkSession spark = SparkSession
                .builder()
                .appName("ReadWriteParquetFiles")
                .config(OUTPUT_PATH, "file:///C:/spark/output_data")
                .config(DEFAULT_FS,true)
                .master("local[*]")
                .config("spark.sql.warehouse.dir", "file:///C:/spark/temp") // Necessary to work around a Windows bug in Spark 2.0.0; omit if you're not on Windows
                .getOrCreate();

        long startTime = System.currentTimeMillis();
        Dataset<Row> sensorsDFCsv = spark.read().format("csv")
                .option("sep", ",")
                .option("inferSchema", "true")
                .option("header", "true")
                .load(CSV_INPUT_FILE);

        sensorsDFCsv.write().mode("append").parquet(PARQUET_DATA_FILE);

//        Displays the content of the DataFrame to stdout
        sensorsDFCsv.show();
        long elapsed = (System.currentTimeMillis() - startTime) / (1000);
        LOGGER.debug("Read and write took "+elapsed + " seconds for "+ sensorsDFCsv.count() + " rows");

        spark.stop();
        return 0;
    }
}
