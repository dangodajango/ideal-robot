package hadoop.learning.project.log.processor.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * The common practice when working with Map-Reduce jobs, is to have a different main class for each job,
 * which allows us to bundle the jobs into a single jar, and switch between them using the main classes,
 * passing different configurations for the different jobs.
 */
public class Post200RangeJob {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        final Configuration configuration = new Configuration();
        final Job job = Job.getInstance(configuration, "Post-200-Range");

        job.setJarByClass(Post200RangeJob.class);

        job.setMapperClass(Post200RangeMapper.class);
        job.setReducerClass(Post200RangeReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
