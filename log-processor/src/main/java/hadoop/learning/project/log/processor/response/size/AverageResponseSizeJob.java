package hadoop.learning.project.log.processor.response.size;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class AverageResponseSizeJob {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        final Configuration configuration = new Configuration();
        final Job job = Job.getInstance(configuration, "Average-Endpoint-Response-Size");

        job.setJarByClass(AverageResponseSizeJob.class);

        job.setMapperClass(AverageResponseSizeMapper.class);
        job.setReducerClass(AverageResponseSizeReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
