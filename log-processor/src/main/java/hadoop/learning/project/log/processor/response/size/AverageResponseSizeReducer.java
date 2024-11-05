package hadoop.learning.project.log.processor.response.size;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.math.BigInteger;

public class AverageResponseSizeReducer extends Reducer<Text, LongWritable, Text, DoubleWritable> {

    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
        BigInteger responseSizeSum = BigInteger.ZERO;
        long iterableSize = 0;
        for (LongWritable responseSize : values) {
            responseSizeSum = responseSizeSum.add(BigInteger.valueOf(responseSize.get()));
            iterableSize++;
        }
        final double averageSize = responseSizeSum.doubleValue() / iterableSize;
        context.write(key, new DoubleWritable(averageSize));
    }
}
