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
 * Mapper for reading hashtags from the file and add them with count 1 to the output.
 *
 */

public class TwitterMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
    private final static LongWritable ONE = new LongWritable(1);

    @Override
    public void map(LongWritable key, Text value, Context output) throws IOException, InterruptedException {
        try {
            //Create the code for the DEL char
            String string = value.toString();
            String DEL = Character.toString((char)127);
            //Replace it in the string
            string = string.replaceAll(DEL, "");
            JsonReader reader = Json.createReader(new BufferedReader(new StringReader(string)));
            JsonObject root = reader.readObject();
            if (root.containsKey("entities")) {
                JsonObject entitites = root.getJsonObject("entities");
                if (entitites.containsKey("hashtags")) {
                    JsonArray hashtags = entitites.getJsonArray("hashtags");
                    for (int i = 0; i < hashtags.size(); i++) {
                        //For each hashtag: read it and add it with count 1, if not null
                        String hashtag = hashtags.getJsonObject(i).getString("text");
                        if (hashtag != null) {
                            output.write(new Text(hashtag), ONE);
                        }
                    }
                }
            }
        } catch (JsonException e) {
            ExceptionGUI.displayExceptionWait(e);
        }
    }
}
