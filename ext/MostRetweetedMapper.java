import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import javax.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by bozhidar on 17.04.17.
 *
 * Mapper that is used for getting the id of a tweeter and the total
 * number of retweets he has.
 *
 */
public class MostRetweetedMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        try {
            //Create the code for the DEL char
            String string = value.toString();
            String DEL = Character.toString((char)127);
            //Replace it in the string
            string = string.replaceAll(DEL, "");
            JsonReader reader = Json.createReader(new BufferedReader(new StringReader(string)));
            JsonObject root = reader.readObject();
            if (root.containsKey("retweeted_status")) {
                JsonObject retweeted = root.getJsonObject("retweeted_status");
                if (retweeted.containsKey("user")) {
                    JsonObject user = retweeted.getJsonObject("user");
                    if (user.containsKey("id_str")) {
                        //Get the id of the tweeter
                        Text id = new Text(String.valueOf(user.get("id_str")));
                        if (retweeted.containsKey("retweet_count")) {
                            //Get the retweet count
                            LongWritable retweet_count = new LongWritable(Integer.parseInt(retweeted.get("retweet_count").toString()));
                            context.write(id, retweet_count);
                        }
                    }
                }
            }
        } catch (JsonException e) {
            ExceptionGUI.displayExceptionWait(e);
        }
    }
}
