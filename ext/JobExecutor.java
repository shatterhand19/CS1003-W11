import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.LocalJobRunner;
import org.apache.hadoop.mapreduce.Job;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by bozhidar on 17.04.17.
 */
public class JobExecutor {
    private String input_path,
            output_path;
    private JobConf conf;
    private FileSystem fileSystem;

    public JobExecutor(String input_path, String output_path) throws IOException {
        this.input_path = input_path;
        this.output_path = output_path;
        conf = new JobConf();
        fileSystem = FileSystem.getLocal(conf);
        fileSystem.delete(new Path(output_path), true);
    }

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
            ExceptionGUI.displayExceptionWithoutWait(e);
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
            ExceptionGUI.displayExceptionWithoutWait(e);
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
            ExceptionGUI.displayExceptionWithoutWait(e);
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
            long start = System.currentTimeMillis();
            countHashtags.waitForCompletion(verbose);
            long totalTime = (System.currentTimeMillis() - start);
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            ExceptionGUI.displayExceptionWithoutWait(e);
        }



    }

    public void ready(String jobName) {
        JOptionPane.showMessageDialog(null,
                "Job " + jobName + " is ready!",
                "Job ready",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
