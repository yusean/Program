package org.proxylog;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.common.utils.HBaseQuaryTools;
import org.domain.ProxyLog;
import org.hadoop.ProxyLogStatistics;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpStatusStatistics {
    private static final Logger L = LoggerFactory.getLogger(HttpStatusStatistics.class);
    private static PropertiesConfiguration config = null;
    private static Configuration conf = null;
    private static final String COLUMN_FAMILY = "LibraryInfo";
    private static final String QUALIFIER_STATUSCODE = "StatusCode";
    private static final String RANGE = "TimeRange.properties";
    private static final String QUALIFIER_TIMESTEMP = "Timestemp";
    private static String[] timeRange = {};
    static {
        conf = HBaseConfiguration.create();
        try {
            config = new PropertiesConfiguration(RANGE);
            timeRange = config.getStringArray("TimeRange");
        } catch (ConfigurationException e) {
            L.error("ConfigurationException:"+ e);
        }
    }
    

    public static void main(String[] args) {
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(QUALIFIER_TIMESTEMP));
        scan.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(QUALIFIER_STATUSCODE));
        //選取時間區間
        FilterList filterList = new FilterList(HBaseQuaryTools.columnFilterList(COLUMN_FAMILY, QUALIFIER_TIMESTEMP, timeRange));
        scan.setFilter(filterList);
        
        try {
            FileSystem hdfs = FileSystem.get(conf);
            //Path inputPath = new Path(hdfs.getWorkingDirectory().toString() + "/input");
            Path outputPath = new Path(hdfs.getWorkingDirectory().toString() + "/output/HttpStatusOutput-" + new DateTime().toString("yyyy-MM-dd HH:mm:ss.sss")); //當下的運行的目錄
            Job job = new Job(conf, "HttpStatusStatistics");
            job.setJarByClass(ProxyLogStatistics.class);
            TableMapReduceUtil.initTableMapperJob("NetFlowProxyLogTest", scan, HttpStatusStatisticsMapper.class, Text.class, IntWritable.class, job);
            job.setReducerClass(HttpStatusStatisticsReducer.class);
            
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            if (hdfs.exists(outputPath))
                hdfs.delete(outputPath, true);
            
            FileOutputFormat.setOutputPath(job, outputPath);
            if (job.waitForCompletion(true)) {
                System.out.println("Job Done!");
                System.exit(0);
            } else {
                System.out.println("Job Failed!");
                System.exit(1);
            }
        } catch (Exception e) {
            L.error("HTable Query fail.", e);
        }

    }

    public static class HttpStatusStatisticsMapper extends TableMapper<Text, IntWritable>{
        private IntWritable value = new IntWritable(1);
        private Text word = new Text();
        String statusCode = "";
        String timeStemp = "";
        @Override
        public void map(ImmutableBytesWritable row, Result columns, Context context) throws IOException {
            timeStemp = new String(columns.getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(QUALIFIER_TIMESTEMP)));
            statusCode = new String(columns.getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(QUALIFIER_STATUSCODE)));
            String[] code = statusCode.split("/");
//            String[] time = timeStemp.split(" ");
//            word.set(time[0]+ "---" + code[1]);
            word.set(timeStemp+ "---" + code[1]);
            try {
                context.write(word, value);
            } catch (InterruptedException e) {
                L.error("HttpStatusStatisticsMapper fail.", e);
                L.error("Row:{}", row.get());
                L.info("statusCode:{}", statusCode);
                context.getCounter(ProxyLog.Counters.ERROR).increment(1);
            }
        }
    }
    
    public static class HttpStatusStatisticsReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
}
