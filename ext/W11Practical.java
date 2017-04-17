import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;

/**
 * Created by bozhidar on 11.04.17.
 */
public class W11Practical {
    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            System.out.println("Usage: java -cp \"lib:bin\" W11Practical <input_path> <output_path>");
            System.exit(1);
        }

        String input_path = args[0];
        String output_path = args[1];
        String temp = "temp";

        // Setup new Job and Configuration
        JobConf config = new JobConf();

        FileSystem fileSystem = FileSystem.getLocal(config);
        fileSystem.delete(new Path(output_path), true);
        fileSystem.delete(new Path(temp), true);

        //config.setOutputValueGroupingComparator(LongWritable.DecreasingComparator.class);

        Job countHashtags = new JobBuilder().createJob(config, "Hashtag Count")
                .setPaths(input_path, temp)
                .setMapper(TwitterMapper.class)
                .setMapperTypes(Text.class, LongWritable.class)
                //.setSort(LongWritable.DecreasingComparator.class)
                .setReducer(TwitterReducer.class)
                .setReducerTypes(Text.class, LongWritable.class).getJob();

        //config.setOutputKeyComparatorClass(Text.Comparator.class);

        Job sortPopular = new JobBuilder().createJob(config, "Most popular")
                .setPaths(temp, output_path)
                .setMapper(MostPopularMapper.class)
                .setMapperTypes(LongWritable.class, Text.class)
                .setSort(LongWritable.DecreasingComparator.class)
                .setReducer(MostPopularReducer.class)
                .setReducerTypes(Text.class, LongWritable.class).getJob();

        try {
            countHashtags.waitForCompletion(true);
            sortPopular.waitForCompletion(true);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
