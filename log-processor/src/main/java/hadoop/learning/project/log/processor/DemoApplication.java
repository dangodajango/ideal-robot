package hadoop.learning.project.log.processor;

import hadoop.learning.project.log.processor.single.purpose.Post200RangeJob;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class DemoApplication {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Post200RangeJob.startJob(new Path(args[0]), new Path(args[1]));
    }
}
