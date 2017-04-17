import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by bozhidar on 17.04.17.
 *
 * Builder pattern.
 *
 */
public class JobBuilder {
    private Job job;

    public JobBuilder createJob(Configuration configuration, String jobName) throws IOException {
        this.job =  Job.getInstance(configuration, jobName);
        return this;
    }

    public JobBuilder setPaths(String input, String output) throws IOException {
        FileInputFormat.setInputPaths(this.job, new Path(input));
        FileOutputFormat.setOutputPath(this.job, new Path(output));
        return this;
    }

    public JobBuilder setMapper(Class mapperClass) {
        this.job.setMapperClass(mapperClass);
        return this;
    }

    public JobBuilder setMapperTypes(Class outputKey, Class outputValue) {
        this.job.setMapOutputKeyClass(outputKey);
        this.job.setMapOutputValueClass(outputValue);
        return this;
    }

    public JobBuilder setSort(Class sortComparator) {
        this.job.setSortComparatorClass(sortComparator);
        return this;
    }

    public JobBuilder setReducer(Class reducerClass) {
        this.job.setReducerClass(reducerClass);
        return this;
    }

    public JobBuilder setReducerTypes(Class outputKey, Class outputValue) {
        this.job.setOutputKeyClass(outputKey);
        this.job.setOutputValueClass(outputValue);
        return this;
    }

    public Job getJob() {
        return job;
    }

    public void runJob() throws InterruptedException, IOException, ClassNotFoundException {
        this.job.waitForCompletion(true);
    }
}
