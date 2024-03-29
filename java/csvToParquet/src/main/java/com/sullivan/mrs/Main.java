package com.sullivan.mrs;

import org.apache.avro.LogicalTypes;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class Main {

    protected static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static final String CSV_INPUT_FILE = "C:\\Users\\mathe\\data\\test_data_FourDays_10hz.csv";
    private static final String PARQUET_OUTPUT_FILE = "C:\\Users\\mathe\\data\\test_data_FourDays_10hz_JAVA.parquet";

    public static void main(String[] args) {

        if (Files.exists(Paths.get(PARQUET_OUTPUT_FILE))) {
            try {
                Files.delete(Paths.get(PARQUET_OUTPUT_FILE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long startTime = System.currentTimeMillis();
        LOGGER.debug("reading CSV");
        System.out.println("reading CSV");
        try (Reader reader = Files.newBufferedReader(Paths.get(CSV_INPUT_FILE))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
            List<String> headerNames = csvParser.getHeaderNames();

            SchemaBuilder.RecordBuilder<Schema> recordBuilder = SchemaBuilder.record("SensorRecord");
            recordBuilder.namespace("com.sullivan.mrs");
            SchemaBuilder.FieldAssembler fieldAssembler = recordBuilder.fields();
            fieldAssembler.name("time").type(LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG))).noDefault();
            for (int i = 1; i < headerNames.size(); i++) {
                fieldAssembler.name(String.format("sensor_%d", i)).type().nullable().doubleType().noDefault();
            }
            Schema avroSchema = (Schema) fieldAssembler.endRecord();

//            Configuration conf = new Configuration();
//            conf.set("fs.s3a.access.key", "ACCESSKEY");
//            conf.set("fs.s3a.secret.key", "SECRETKEY");
            //Below are some other helpful settings
            //conf.set("fs.s3a.endpoint", "s3.amazonaws.com");
            //conf.set("fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider");
            //conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()); // Not needed unless you reference the hadoop-hdfs library.
            //conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName()); // Uncomment if you get "No FileSystem for scheme: file" errors

            Path filePath = new Path(PARQUET_OUTPUT_FILE);
            int blockSize = 1024;
            int pageSize = 65535;
            try (
                    ParquetWriter<GenericData.Record> parquetWriter = AvroParquetWriter.
                            <GenericData.Record>builder(filePath)
                            .withSchema(avroSchema)
                            .withCompressionCodec(CompressionCodecName.SNAPPY)
//                        .withConf(conf)
                            .withPageSize(pageSize)
                            .build();) {

                csvParser.getRecords().stream().forEach(obj -> {
                    GenericData.Record record = new GenericData.Record(avroSchema);
                    SensorRecords sensorData = new SensorRecords(obj);
                    record.put(0, sensorData.getTime());
                    for (int i = 1; i < headerNames.size(); i++) {
                        record.put(i, sensorData.getSensor(i - 1));
                    }
                    try {
                        parquetWriter.write(record);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } catch (java.io.IOException e) {
                System.out.println(String.format("Error writing parquet file %s", e.getMessage()));
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long elapsed = (endTime - startTime) / (1000);
        System.out.println("wrote Parquet " + elapsed);
        LOGGER.debug("wrote Parquet");
    }

    static class SensorRecords {
        public long time;
        List<Double> sensorValues = new ArrayList<>();

        public SensorRecords(CSVRecord obj) {
            time = (long)Double.parseDouble(obj.get(0));

            for (int i =1; i < obj.size(); i++) {
                sensorValues.add(Double.parseDouble(obj.get(i)));
            }
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public double getSensor(int index) {
            return sensorValues.get(index);
        }
    }
}
