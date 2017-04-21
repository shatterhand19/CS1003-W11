import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.LocalJobRunner;
import org.apache.hadoop.mapreduce.Job;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by bozhidar on 17.04.17.
 *
 * Class that provides all methods for the creation and execution
 * of the different job functions used by the program.
 *
 */
public class JobExecutor {
    private String input_path,
            output_path;
    private JobConf conf;
    private FileSystem fileSystem;

    /**
     * Constructor.
     * @param input_path is the input path for the jobs this executor will perform
     * @param output_path is the output path for the jobs this executor will perform
     * @throws IOException
     */
    public JobExecutor(String input_path, String output_path) throws IOException {
        this.input_path = input_path;
        this.output_path = output_path;

        //Create new configuration
        conf = new JobConf();

        //Delete the output path
        fileSystem = FileSystem.getLocal(conf);
        fileSystem.delete(new Path(output_path), true);
    }
    /*
     * The four methods above have the same structure:
     * Create a job with the appropriate parameters and
     * then execute it.
    */
    public void simpleCount(boolean verbose) throws IOException {
        Job countHashtags = new JobBuilder().createJob(conf, "Hashtag Count")
                .setPaths(input_path, output_path)
                .setMapper(TwitterMapper.class)
                .setMapperTypes(Text.class, LongWritable.class)
                .setReducer(TwitterReducer.class)
                .setReducerTypes(Text.class, LongWritable.class).getJob();

        try {
            countHashtags.waitForCompletion(verbose);
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            ExceptionGUI.displayException(e);
        }
    }

    public void mostPopular(boolean verbose) throws IOException {
        Job sortPopular = new JobBuilder().createJob(conf, "Most popular")
                .setPaths(input_path, output_path)
                .setMapper(MostPopularMapper.class)
                .setMapperTypes(LongWritable.class, Text.class)
                .setSort(LongWritable.DecreasingComparator.class)
                .setReducer(MostPopularReducer.class)
                .setReducerTypes(Text.class, LongWritable.class).getJob();

        try {
            sortPopular.waitForCompletion(verbose);
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            ExceptionGUI.displayException(e);
        }
    }

    public void mostRetweeted(boolean verbose) throws IOException {
        Job mostRetweeted = new JobBuilder().createJob(conf, "Most retweeted")
                .setPaths(input_path, output_path)
                .setMapper(MostRetweetedMapper.class)
                .setMapperTypes(Text.class, LongWritable.class)
                //.setSort(LongWritable.DecreasingComparator.class)
                .setReducer(MostRetweetedReducer.class)
                .setReducerTypes(LongWritable.class, Text.class).getJob();

        try {
            mostRetweeted.waitForCompletion(verbose);
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            ExceptionGUI.displayException(e);
        }
    }

    public void simpleCountGraph(boolean verbose, int maxJobs) throws IOException {
        Job countHashtags = new JobBuilder().createJob(conf, "Hashtag Count")
                .setPaths(input_path, output_path)
                .setMapper(TwitterMapper.class)
                .setMapperTypes(Text.class, LongWritable.class)
                .setReducer(TwitterReducer.class)
                .setReducerTypes(Text.class, LongWritable.class).getJob();

        LocalJobRunner.setLocalMaxRunningMaps(countHashtags, maxJobs);
        try {
            countHashtags.waitForCompletion(verbose);
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            ExceptionGUI.displayException(e);
        }
    }

    /**
     * Displays a popup with "Job -job_name- is ready" message
     * @param jobName is the name of the job
     */
    public void ready(String jobName) {
        JOptionPane.showMessageDialog(null,
                "Job " + jobName + " is ready!",
                "Job ready",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
