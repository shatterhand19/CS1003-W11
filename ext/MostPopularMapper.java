import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by bozhidar on 16.04.17.
 *
 * Mapper for sorting the tweets by some count.
 *
 */
public class MostPopularMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //Scan the line
        Scanner scanner = new Scanner(value.toString());

        //Get the values
        Text hashtag = new Text(scanner.next());
        LongWritable count = new LongWritable(scanner.nextLong());

        //Write them to the output
        context.write(count, hashtag);
    }
}
