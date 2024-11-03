## Log processor

### Description:

### Prerequisites:

### Expected behaviour:

### Notes:

If we want to execute a Map-Reduce logic we need to wrap it in a **job**. A job is the smallest unit of work which
Hadoop is managing.

The components which take part in this job execution are the following:

1. **Resource manager** - It's job is to keep track of the resources (cpu, memory, disk) on each registered node. When
   a new job has to be executed, it will look for a node with sufficient resources, and it will schedule the job on that
   node.
   Well, it will not schedule the job directly, but rather a piece of the whole job, it will split it into smaller
   tasks, based on the input data, and it will schedule these smaller tasks.
2. **Node managers** - These components run on most nodes in the Hadoop's cluster, essentially only on the nodes which
   can execute a task(name node cannot execute tasks), and their job is to report on the resource availability to the **resource manager**, and they
   execute the given tasks when they are assigned to them.
3. 

The mappers and reducers are wrapped in a **job**, which is the smallest unit of work in Hadoop. 


