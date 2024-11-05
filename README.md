## Hadoop log processor

The idea behind this project is to learn more about the basics of Hadoop.

The **log-generator** module will be producing the "big data", which will be processed later on with a Map-Reduce
functions.

The **log-processor** module will be processing the generated data, using Map-Reduce functions.

_NB! The code style is based on the defensive programming paradigm / tiger style programming, assuring the correctness
of the application during the different stages._

### How to run:

1. Make sure you are using Java 8 JDK, and Hadoop is configured.
2. From the root directory, execute the following command:

```console 
mvn clean package
```

3. From the root directory, run the log-generator jar first:

```console
java -jar log-generator/target/log-generator.jar
```

4. From the root directory, run the log-processor jar using Hadoop.
   You should specify the main class for the job you want to execute and the input and output directories path on the
   HDFS as command line arguments.

   NB! The output directory path must be a non-existing directory on the HDFS.

```console
hadoop jar log-processor/target/log-processor.jar hadoop/learning/project/log/processor/response/size/AverageResponseSizeJob /logs /output
```

5. Check the results in the output directory on the HDFS:

```console
hdfs dfs -ls /output
hdfs dfs cat /output/{placeholder_for_result_file}
```

