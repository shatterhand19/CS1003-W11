import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by bozhidar on 16.04.17.
 */
public class MostPopularReducer extends Reducer<LongWritable, Text, Text, LongWritable> {
    @Override
    protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text  value : values) {
            //System.out.println(value + " " + key.toString());
            context.write(value, key);
        }
    }
}
