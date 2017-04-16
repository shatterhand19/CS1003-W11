import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by bozhidar on 11.04.17.
 */
public class W11Practical {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java -cp \"lib/*:bin\" W11Practical <input_path> <output_path>");
            System.exit(1);
        }

        String input_path = args[0];
        String output_path = args[1];
        String temp = "temp";

        // Setup new Job and Configuration
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Hashtag Count");
        //job.setJarByClass(W11Practical.class);


        FileSystem fileSystem = FileSystem.getLocal(conf);
        fileSystem.delete(new Path(output_path), true);
        //fileSystem.delete(new Path(temp), true);

        // Specify input and output paths
        FileInputFormat.setInputPaths(job, new Path(input_path));
        FileOutputFormat.setOutputPath(job, new Path(temp));

        // Set our own ScanWordsMapper as the mapper
        job.setMapperClass(TwitterMapper.class);

        // Specify output types produced by mapper (words with count of 1)
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        // The output of the reducer is a map from unique words to their total counts.
        job.setReducerClass(TwitterReducer.class);

        // Specify the output types produced by reducer (words with total counts)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        /**
         * The most popular
         */
        Job jobPopular = Job.getInstance(conf, "Most Popular");
        //jobPopular.setJarByClass(W11Practical.class);

        // Specify input and output paths
        FileInputFormat.setInputPaths(jobPopular, new Path(temp));
        FileOutputFormat.setOutputPath(jobPopular, new Path(output_path));

        // Set our own ScanWordsMapper as the mapper
        jobPopular.setMapperClass(MostPopularMapper.class);

        // Specify output types produced by mapper (words with count of 1)
        jobPopular.setMapOutputKeyClass(LongWritable.class);
        jobPopular.setMapOutputValueClass(Text.class);

        // The output of the reducer is a map from unique words to their total counts.
        jobPopular.setReducerClass(MostPopularReducer.class);

        // Specify the output types produced by reducer (words with total counts)
        jobPopular.setOutputKeyClass(Text.class);
        jobPopular.setOutputValueClass(LongWritable.class);

        try {
            jobPopular.waitForCompletion(true);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
