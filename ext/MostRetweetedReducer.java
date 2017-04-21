import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by bozhidar on 17.04.17.
 *
 * Reducer for the most retweeted user;
 * It just sums the number of all of the retweets for all his tweets.
 *
 */
public class MostRetweetedReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
    @Override
    public void reduce(Text key, Iterable<LongWritable> values, Context output) throws IOException, InterruptedException {
        int sum = 0;
        for (LongWritable val : values) {
            sum += val.get();
        }
        output.write(key, new LongWritable(sum));
    }
}
