package se.ju.myapplication;

import android.os.AsyncTask;
import android.net.Uri.Builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Connection {
    private final Builder builder = new Builder();
    private final ObjectMapper mapper = new ObjectMapper();

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
        List<Meme> response = (List<Meme>) new GetRequest(new URL(reqUrl), new TypeReference<List<Meme>>() {}).execute().get();
        return response;
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
        List<MemeTemplate> response = (List<MemeTemplate>) new GetRequest(new URL(reqUrl), new TypeReference<List<MemeTemplate>>() {}).execute().get();
        return response;
    }

    public boolean createUser(String username, String password) throws JsonProcessingException, MalformedURLException, ExecutionException, InterruptedException {
        builder.appendPath("users");

        HashMap<String, String> user = new HashMap<String, String>();
        user.put("username", username);
        user.put("password", password);

        String reqUrl = builder.build().toString();
        new PostRequest(new URL(reqUrl), mapper.writeValueAsString(user)).execute().get();
        return true;
    }
}

class GetRequest extends AsyncTask {
    private final ObjectMapper mapper = new ObjectMapper();

    GetRequest(URL url, TypeReference type)
    {
        this.url = url;
        this.type = type;
    }
    private URL url;
    private TypeReference type;

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            Object result = mapper.readValue(this.url, this.type);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}

class PostRequest extends AsyncTask {
    private final ObjectMapper mapper = new ObjectMapper();

    PostRequest(URL url, String jsonData) {
        this.url = url;
        this.jsonData = jsonData;
    }

    private URL url;
    private String jsonData;

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            OutputStream os = conn.getOutputStream();
            os.write(jsonData.getBytes("UTF-8"));
            os.close();


/*            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String result = "";
            String line = "";
            while ((line = new BufferedReader(new InputStreamReader(in)).readLine()) != null)
            {
                System.out.println(line);
                result.concat(line).concat("\n");
            }


            in.close();
            conn.disconnect();*/

            System.out.println("KUUUK");

            StringBuilder sb = new StringBuilder();
            int HttpResult = conn.getResponseCode();
            System.out.println(HttpResult);
/*            if (HttpResult == HttpURLConnection.HTTP_OK) {*/
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println("a");
                    sb.append(line + "\n");
                }
                br.close();
                System.out.println("" + sb.toString());
/*            }*/




            /*System.out.println(conn.getcont);*/
            System.out.println("KUUUK");

            return true;
        }
        catch (Exception e) {
            System.out.println("INTE KUK");
            e.printStackTrace();
            System.out.println("INTE KUK");
            return null;
        }
    }
}