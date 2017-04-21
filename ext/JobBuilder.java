import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by bozhidar on 17.04.17.
 * <p>
 * Job builder using the build pattern.
 */
public class JobBuilder {
    private Job job;

    /**
     * Create a job from configuration and job name.
     *
     * @param configuration is the configuration the job uses
     * @param jobName       is the name of the job
     * @return the job
     * @throws IOException
     */
    public JobBuilder createJob(Configuration configuration, String jobName) throws IOException {
        this.job = Job.getInstance(configuration, jobName);
        return this;
    }

    /**
     * Sets the input and output paths of the job.
     *
     * @param input  is the input path
     * @param output is the output path
     * @return the job
     * @throws IOException
     */
    public JobBuilder setPaths(String input, String output) throws IOException {
        FileInputFormat.setInputPaths(this.job, new Path(input));
        FileOutputFormat.setOutputPath(this.job, new Path(output));
        return this;
    }

    /**
     * Sets the mapper fro the job.
     *
     * @param mapperClass is the mapper class the job uses
     * @return
     */
    public JobBuilder setMapper(Class mapperClass) {
        this.job.setMapperClass(mapperClass);
        return this;
    }

    /**
     * Sets the mapper types for the job.
     *
     * @param outputKey   is the output key of the mapper
     * @param outputValue is the output value of te mapper
     * @return the job
     */
    public JobBuilder setMapperTypes(Class outputKey, Class outputValue) {
        this.job.setMapOutputKeyClass(outputKey);
        this.job.setMapOutputValueClass(outputValue);
        return this;
    }

    /**
     * Sets a sorting comparator. Used when sorting in decreasing order.
     *
     * @param sortComparator is the sort comparator class
     * @return the job
     */
    public JobBuilder setSort(Class sortComparator) {
        this.job.setSortComparatorClass(sortComparator);
        return this;
    }

    /**
     * Sets a reducers for the job.
     *
     * @param reducerClass is the reducer class the job is using
     * @return the job
     */
    public JobBuilder setReducer(Class reducerClass) {
        this.job.setReducerClass(reducerClass);
        return this;
    }

    /**
     * Sets the reducer types for the job.
     *
     * @param outputKey   is the output key of the reducer
     * @param outputValue is the output value of the reducer
     * @return the job
     */
    public JobBuilder setReducerTypes(Class outputKey, Class outputValue) {
        this.job.setOutputKeyClass(outputKey);
        this.job.setOutputValueClass(outputValue);
        return this;
    }

    /**
     * Returns the job.
     *
     * @return the job
     */
    public Job getJob() {
        return job;
    }

    /**
     * Runs the job. (Currently not used, but in my opinion a good practice to have).
     *
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void runJob() throws InterruptedException, IOException, ClassNotFoundException {
        this.job.waitForCompletion(true);
    }
}
