package se.ju.myapplication;

import android.os.AsyncTask;
import android.net.Uri.Builder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Connection extends AsyncTask {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Builder builder = new Builder();

    class Request<T> {
        Request(URL url, TypeReference<T> type)
        {
            this.url = url;
            this.type = type;
        }
        private URL url;
        private TypeReference<T> type;

        public URL getUrl() {
            return url;
        }

        public TypeReference<T> getType() {
            return type;
        }
    }

    Connection()
    {
        builder.scheme("http").authority("schpoop.eu-central-1.elasticbeanstalk.com");
    }

    public List<Meme> getMemes(String name, String templateId, String username, Integer pageSize, Integer page) throws MalformedURLException, ExecutionException, InterruptedException {
        builder.appendPath("memes");

        if(name != null) {
            builder.appendQueryParameter("name", name);
        }
        if(templateId != null) {
            builder.appendQueryParameter("templateId", templateId);
        }
        if(username != null) {
            builder.appendQueryParameter("username", username);
        }
        if(pageSize != null) {
            builder.appendQueryParameter("pageSize", pageSize.toString());
        }
        if(page != null) {
            builder.appendQueryParameter("page", page.toString());
        }
        String reqUrl = builder.build().toString();
        Request<List<Meme>> request = new Request<List<Meme>>(new URL(reqUrl), new TypeReference<List<Meme>>() {
        });
        return (List<Meme>) execute(request).get();
    }

    public List<MemeTemplate> getMemeTemplates(String name, String username, Integer pageSize, Integer page) throws MalformedURLException, ExecutionException, InterruptedException {
        builder.appendPath("memeTemplates");

        if(name != null) {
            builder.appendQueryParameter("name", name);
        }
        if(username != null) {
            builder.appendQueryParameter("username", username);
        }
        if(pageSize != null) {
            builder.appendQueryParameter("pageSize", pageSize.toString());
        }
        if(page != null) {
            builder.appendQueryParameter("page", page.toString());
        }
        String reqUrl = builder.build().toString();
        Request<List<MemeTemplate>> request = new Request<List<MemeTemplate>>(new URL(reqUrl), new TypeReference<List<MemeTemplate>>() {});
        return (List<MemeTemplate>) execute(request).get();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        Request request = (Request)objects[0];
        try {
            Object result = mapper.readValue(request.getUrl(), request.getType());
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
