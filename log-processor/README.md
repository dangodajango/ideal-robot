## Log processor

### Description:

The **log-processor** contains all the Map-Reduce jobs, which are analysing the HTTP logs provided by the *
*log-generator** component.

### Prerequisites:

- Have a running Hadoop cluster.
- Pass the input directory path when the jar is executed by Hadoop. It should contain the data prepared by the **log
  generator**.
- Pass the output directory path when the jar is executed by Hadoop. It should be a non-existent directory.

### Expected behaviour:

- It will calculate the number of HTTP POST requests in the 200 ranges.

### Notes:

If we want to execute a Map-Reduce logic we need to wrap it in a **job**. A job is the smallest unit of work which
Hadoop is managing.

#### The components of a job execution:

1. **Resource manager**:
    - It keeps track of all the resources(CPU, Disk, Memory) available in the Hadoop cluster, whenever an application
      which wants to execute in the cluster, it will look for the most appropriate node, and reserve resources there.
    - It schedules the submitted jobs, splitting them into smaller tasks (map, reduce) and spawns an **application
      master**, which handles the tasks.
    - It allocates containers across the nodes, which are used to run the tasks in isolation, and it provides these
      containers to the **application master**.
    - It monitors the health of each node, if a node fails, it will redirect the tasks to a healthy node.
2. **Application master**:
    - It requests resources, in the form of containers for the tasks which he has been given to execute, and it keeps
      track of the resources each task has, making sure they are sufficient.
    - It schedules tasks to run on different nodes, coordinating it with the **node managers**.
    - It handles any task failures, by retrying or redirecting them to different nodes.
3. **Node manager**:
    - It executes the containers, running the actual tasks, making sure that they are completely in isolation, and it
      monitors their health.
    - It cleans up the resources after the task has finished.

#### The flow of job execution is the following:

1. The client submits a job to the **resource manager**, and the resource manager will spawn
   an **application master** for that specific job.
2. The application master requests resources from the resource manager for the execution of the map-reduce tasks.
3. The resource manager provides resources in the form of **containers** on the different nodes, based on the available
   resources.
4. The application master assigns different tasks to the allocated containers, it sends the task details - is it a map,
   or reduce task, where the input for the task is located, and any other configuration to the **node manager**.
5. The node manager launches the container and executes the task, when it's done it reports the status to the
   application mater.
6. If something fails, the application master can request new containers, or retry the tasks.
7. After all tasks have finished successfully, the application master informs the resource manager, and the reserved
   containers are released.