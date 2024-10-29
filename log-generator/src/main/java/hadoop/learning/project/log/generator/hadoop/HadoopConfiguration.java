package hadoop.learning.project.log.generator.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@org.springframework.context.annotation.Configuration
public class HadoopConfiguration {

    @Bean
    public FileSystem hadoopFileSystem(@Value("${hdfs.url}") final String hdfsUrl) throws IOException {
        final Configuration hadoopConfiguration = new Configuration();
        hadoopConfiguration.set("fs.defaultFS", hdfsUrl);
        hadoopConfiguration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        return FileSystem.get(hadoopConfiguration);
    }
}
