package hadoop.learning.project.log.processor.single.purpose;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * The first key-value pair(input pair) is Text-IntWritable, which is the output pair produced by the mapper.
 * <br>
 * The second key-value pair(output pair) is Text-IntWritable, which is the final output that this reducer will create.
 * This pair will be handled to Hadoop, and it will write it to a file on the HDFS(configured during the job creation).
 * <br><br>
 * When the job is executed, and all the mappers have processed the data, the following steps will happen:
 * <ol>
 *     <li>
 *         <b>Partitioning:</b> A job can be configured to launch multiple reducers, so they can be scaled across different nodes.
 *          This is the "load balancing" step, using a hashing function, Hadoop will distribute the key-value pairs between the available reducers.
 *          In this example we only receive "POST_200_RANGE_COUNT", but in cases where the mapper was also generating other key-value pairs like
 *          "GET_200_RANGE_COUNT", and we had multiple of reducers, one of them will process the POST key-value pairs, the other one the GET key-value pairs.
 *     </li>
 *     <li>
 *         <b>Shuffling:</b> After the <b>partitioning</b> phase, where Hadoop has mapped the key-value pairs to the available reducers,
 *         it will start sending them across the network.
 *     </li>
 * </ol>
 */
public class Post200RangeReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int numberOfPost200RangeRequests = 0;
        for (IntWritable value : values) {
            numberOfPost200RangeRequests += value.get();
        }
        context.write(key, new IntWritable(numberOfPost200RangeRequests));
    }
}
