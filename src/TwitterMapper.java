import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import javax.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by bozhidar on 11.04.17.
 *
 */

public class TwitterMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
    private final static LongWritable ONE = new LongWritable(1);
    @Override
    public void map(LongWritable key, Text value, Context output) throws IOException, InterruptedException {
        JsonReader reader = Json.createReader(new BufferedReader(new StringReader(value.toString())));
        JsonObject root = reader.readObject();
        if (!root.containsKey("entities")) {
            //Do error messages
        } else {
            JsonObject entitites = root.getJsonObject("entities");
            JsonArray hashtags = entitites.getJsonArray("hashtags");
            for (int i = 0; i < hashtags.size(); i++) {
                String hashtag = hashtags.getJsonObject(i).getString("text");
                output.write(new Text(hashtag), ONE);
            }
        }
    }
}
