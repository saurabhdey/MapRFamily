import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;


public class ParkingStatewiseAmt {

	public static class Map extends MapReduceBase implements
	Mapper<LongWritable, Text, Text, IntWritable> {

		@Override
		public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {

			int fine_amt = 0 ; 
			String str ;
			boolean special;
			if (key.get() > 0) {
				String[] tokenarr = value.toString().split(",");
				value.set(tokenarr[2]);
				str = tokenarr[5];
				special =  str.contains("\""); 
				if (!special){
					fine_amt = Integer.parseInt(tokenarr[7]);
					output.collect(value, new IntWritable(fine_amt));}

			}
		}
	}
	public static class Reduce extends MapReduceBase implements
	Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
						throws IOException {
			int sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}

			output.collect(key, new IntWritable(sum));
		}
	}

	public static void main(String[] args) throws Exception {

		JobConf conf = new JobConf(ParkingStatewiseAmt.class);
		conf.setJobName("Finetotals");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(Map.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		JobClient.runJob(conf);

	}
}
