package hadoop.learning.project.log.processor.response.size;

import hadoop.learning.project.log.processor.model.LogLine;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AverageResponseSizeMapper extends Mapper<Object, Text, Text, LongWritable> {

    @Override
    protected void map(Object key, Text value, Mapper<Object, Text, Text, LongWritable>.Context context) throws IOException, InterruptedException {
        final LogLine logLine = LogLine.mapToLogLine(value.toString());
        final String resourcePath = logLine.getResource().getResourcePath();
        if (resourcePath.substring(1).split("/").length == 1) {
            final Text resourcePathKey = new Text(logLine.getResource().getResourcePath());
            final LongWritable responseSize = new LongWritable(logLine.getResponseSize());
            context.write(resourcePathKey, responseSize);
        }
    }
}
