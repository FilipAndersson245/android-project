package se.ju.myapplication;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Connection extends AsyncTask {
    @Override
    protected Object doInBackground(Object... arg0) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL url = new URL("http://schpoop.eu-central-1.elasticbeanstalk.com/memes/");
            List<Meme> map = mapper.readValue(url, new TypeReference<List<Meme>>(){});

            System.out.println(mapper.writeValueAsString(map));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
