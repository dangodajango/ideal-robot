package hadoop.learning.project.log.processor.single.purpose;

import hadoop.learning.project.log.processor.model.LogLine;
import hadoop.learning.project.log.processor.model.Resource;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

import static hadoop.learning.project.log.processor.model.LogLine.mapToLogLine;

/**
 * The first key-value pair(input pair) is Object-Text. Object is the offset of the current line from the beginning of the file.
 * Text is the current line in a text format.
 * <br>
 * The second key-value pair(output pair) is Text-IntWritable. Text specifies that the key will be in a text format.
 * IntWritable is one of the many possible values that Hadoop supports, it indicates that the value will be an integer.
 * <br> <br>
 * When we execute a job, which contains a mapper, the following things are happening:
 * <ol>
 *     <li>
 *         Hadoop splits the input data into smaller pieces, known as <b>input splits</b>.
 *         Each split is typically a block of data stored in the HDFS.
 *     </li>
 *     <li>
 *         The mapper will read the input splits independently, so if we have multiple mappers receiving the same lines from a HDFS file,
 *         each of them will read the file independently to the others.
 *     </li>
 *     <li>
 *         The mapper will process all the lines from the given file, and for each line, it will produce one or more key-value pairs.
 *     </li>
 *     <li>
 *         After a key-value pair is produced by the mapper, Hadoop will buffer it in memory and periodically write to disk.
 *         When all the mappers finish their execution the produced pairs will be sorted, aggregated and made ready to be processed by the reducers.
 *     </li>
 * </ol>
 * <br>
 * THe preferred practice is to create separate mapper-reducer pairs based on the business logic.
 * Or if some mappers are reading the same data, and producing the same key-value pairs, the mappers can be combined into a single one,
 * which will optimise the job execution because it will read the data once, rather than reading it for each mapper.
 * But in the reducer, there has to be conditional logic in order to know which mapper produced the pair.
 */
public class Post200RangeMapper extends Mapper<Object, Text, Text, IntWritable> {

    private static final Text KEY = new Text("POST_200_RANGE_COUNT");

    private static final IntWritable VALUE = new IntWritable(1);

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        LogLine logLine = mapToLogLine(value.toString());
        if (isPostRequest(logLine.resource()) && isIn200Range(logLine.statusCode())) {
            context.write(KEY, VALUE);
        }
    }

    private boolean isPostRequest(Resource resource) {
        return resource.httpVerb().equals("POST");
    }

    private boolean isIn200Range(int statusCode) {
        return statusCode >= 200 && statusCode <= 299;
    }
}
