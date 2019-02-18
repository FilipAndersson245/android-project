package se.ju.myapplication;

import android.net.Uri.Builder;
import android.support.v4.util.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Connection {
    private static final Connection ourInstance = new Connection();

    static Connection getInstance() {
        return ourInstance;
    }

    private Builder builder;
    private final ObjectMapper mapper = new ObjectMapper();
    private String JWT = null;
    private String signedInUsername = null;

    Connection() {
        this.resetBuilder();
    }

    private void resetBuilder() {
        builder = new Builder().scheme("http").authority("schpoop.eu-central-1.elasticbeanstalk.com");
    }

    private void request(final String method, final Builder urlBuilder, final String body, final TypeReference returnType, final boolean authorization, final Consumer<Object> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlBuilder.build().toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestMethod(method);

                    if (authorization) {
                        String a = "Bearer " + JWT;
                        System.out.println(a);
                        conn.setRequestProperty("Authorization", a);
                    }

                    if (body != null) {
                        OutputStream os = conn.getOutputStream();
                        os.write(body.getBytes("UTF-8"));
                        os.close();
                    }

                    int resultRange = (conn.getResponseCode() / 100) * 100;

                    if (resultRange == 200) {
//                        callbackNotify.call(mapper.readValue(conn.getInputStream(), returnType));
                        callback.accept(mapper.readValue(conn.getInputStream(), returnType));
                    } else {
                        if (resultRange == 400) {
                            throw new Exception(mapper.readValue(conn.getErrorStream(), Error.class).getError());
                        }
                        throw new Exception("Error making request.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // ============================== USER ==============================
    // ==================================================================

    public void createUser(String username, String password, Consumer<Object> callback) throws JsonProcessingException {
        builder.appendPath("users");
        User user = new User(username, password);

        request("POST", builder, mapper.writeValueAsString(user), new TypeReference<Void>() {
        }, false, callback);

        resetBuilder();
    }

    public void getUsers(String username, Integer pageSize, Integer page, Consumer<Object> callback) {
        builder.appendPath("users");

        if (username != null) {
            builder.appendQueryParameter("username", username);
        }
        if (pageSize != null) {
            builder.appendQueryParameter("pageSize", pageSize.toString());
        }
        if (page != null) {
            builder.appendQueryParameter("page", page.toString());
        }

        request("GET", builder, null, new TypeReference<String>() {
        }, false, callback);

        resetBuilder();
    }

    public void updateUserPassword(String username, final String newPassword, Consumer<Object> callback) throws JsonProcessingException {
        builder.appendPath("users");
        builder.appendPath(username);

        HashMap<String, String> body = new HashMap<String, String>() {{
            put("password", newPassword);
        }};

        request("PATCH", builder, mapper.writeValueAsString(body), new TypeReference<Void>() {
        }, true, callback);

        resetBuilder();
    }

    public void deleteUser(String username, Consumer<Object> callback) throws JsonProcessingException {
        builder.appendPath("users");
        builder.appendPath(username);

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true, callback);

        resetBuilder();
    }

    public void signInUser(final String username, String password, Consumer<Object> callback) throws JsonProcessingException {
        builder.appendPath("sessions");

        final Session session = new Session("password", username, password);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(builder.build().toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestMethod("POST");

                    OutputStream os = conn.getOutputStream();
                    os.write(mapper.writeValueAsString(session).getBytes("UTF-8"));
                    os.close();

                    StringBuilder sb = new StringBuilder();
                    int resultRange = (conn.getResponseCode() / 100) * 100;

                    if (resultRange == 200) {
                        System.out.println("YES");
                        JWT = mapper.readValue(conn.getInputStream(), SessionResponse.class).getAccessToken();
                        signedInUsername = username;
                        callback.accept(mapper.readValue(conn.getInputStream(), Void.class));
                    } else {
                        System.out.println("NO");
                        JWT = null;
                        if (resultRange == 400) {
                            throw new Exception(mapper.readValue(conn.getErrorStream(), Error.class).getError());
                        }
                        throw new Exception("Error making request.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        resetBuilder();
    }

    // ============================== MEMES =============================
    // ==================================================================

    public void createMeme(Integer templateId, String username, String name, String imageSource, String topText, String bottomText, Consumer<Object> callback) throws JsonProcessingException {
        builder.appendPath("memes");

        NewMeme newMeme = new NewMeme(templateId, username, imageSource);

        if (name != null) {
            newMeme.setName(name);
        }
        if (topText != null) {
            newMeme.setTopText(topText);
        }
        if (bottomText != null) {
            newMeme.setBottomText(bottomText);
        }

        request("POST", builder, mapper.writeValueAsString(newMeme), new TypeReference<Void>() {
        }, true, callback);

        resetBuilder();
    }

    public void getMemes(String name, Integer templateId, String username, Integer pageSize, Integer page, Consumer<Object> callback) {
        builder.appendPath("memes");

        if (name != null) {
            builder.appendQueryParameter("name", name);
        }
        if (templateId != null) {
            builder.appendQueryParameter("templateId", templateId.toString());
        }
        if (username != null) {
            builder.appendQueryParameter("username", username);
        }
        if (pageSize != null) {
            builder.appendQueryParameter("pageSize", pageSize.toString());
        }
        if (page != null) {
            builder.appendQueryParameter("page", page.toString());
        }

        request("GET", builder, null, new TypeReference<List<Meme>>() {
        }, false, callback);

        resetBuilder();
    }

    public void getMeme(Integer memeId, Consumer<Object> callback) {
        builder.appendPath("memes");
        builder.appendPath(memeId.toString());

        request("GET", builder, null, new TypeReference<Meme>() {
        }, false, callback);

        resetBuilder();
    }

    public void deleteMeme(Integer memeId, Consumer<Object> callback) {
        builder.appendPath("memes");
        builder.appendPath(memeId.toString());

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true, callback);

        resetBuilder();
    }

    // ========================== MEMETEMPLATES =========================
    // ==================================================================

    public void createMemeTemplate(final String name, final String username, Integer pageSize, Integer page, final Consumer<Object> callback) throws MalformedURLException, ExecutionException, InterruptedException {
        builder.appendPath("memetemplates");

        if (name != null) {
            builder.appendQueryParameter("name", name);
        }
        if (username != null) {
            builder.appendQueryParameter("username", username);
        }
        if (pageSize != null) {
            builder.appendQueryParameter("pageSize", pageSize.toString());
        }
        if (page != null) {
            builder.appendQueryParameter("page", page.toString());
        }

//        request("GET", builder, null, new TypeReference<List<MemeTemplate>>() {
//        }, false);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MultipartUtility multipart = new MultipartUtility(builder.build().toString(), "UTF-8", callback);
                    multipart.addFormField("username", username);
                    if (name != null) {
                        multipart.addFormField("name", name);
                    }
                    multipart.addFilePart("image", new File("https://i.imgur.com/zNU9fe8.png"));
                    multipart.addHeaderField("Authorization", "Bearer " + JWT);

                    multipart.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        resetBuilder();
    }

    public void getMemeTemplates(String name, String username, Integer pageSize, Integer page, Consumer<Object> callback) throws MalformedURLException, ExecutionException, InterruptedException {
        builder.appendPath("memetemplates");

        if (name != null) {
            builder.appendQueryParameter("name", name);
        }
        if (username != null) {
            builder.appendQueryParameter("username", username);
        }
        if (pageSize != null) {
            builder.appendQueryParameter("pageSize", pageSize.toString());
        }
        if (page != null) {
            builder.appendQueryParameter("page", page.toString());
        }

        request("GET", builder, null, new TypeReference<List<MemeTemplate>>() {
        }, false, callback);

        resetBuilder();
    }

    public void getMemeTemplate(Integer templateId, Consumer<Object> callback) {
        builder.appendPath("memetemplates");
        builder.appendPath(templateId.toString());

        request("GET", builder, null, new TypeReference<MemeTemplate>() {
        }, false, callback);

        resetBuilder();
    }

    public void deleteMemeTemplate(Integer templateId, Consumer<Object> callback) {
        builder.appendPath("memetemplates");
        builder.appendPath(templateId.toString());

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true, callback);

        resetBuilder();
    }

    // ============================== VOTES =============================
    // ==================================================================

    public void vote(Integer memeId, String username, Integer vote, Consumer<Object> callback) throws JsonProcessingException {
        builder.appendPath("votes");
        builder.appendPath(memeId.toString());

        NewVote newVote = new NewVote(username, vote);

        System.out.println(builder.toString());

        request("PUT", builder, mapper.writeValueAsString(newVote), new TypeReference<Vote>() {
        }, true, callback);

        resetBuilder();
    }

    public void getVote(Integer memeId, final String username, Consumer<Object> callback) throws JsonProcessingException {
        builder.appendPath("votes");
        builder.appendPath(memeId.toString());

        HashMap<String, String> body = new HashMap<String, String>() {{
            put("username", username);
        }};

        request("GET", builder, null, new TypeReference<Vote>() {
        }, false, callback);

        resetBuilder();
    }

    public void removeVote(Integer memeId, final String username, Consumer<Object> callback) throws JsonProcessingException {
        builder.appendPath("votes");
        builder.appendPath(memeId.toString());

        HashMap<String, String> body = new HashMap<String, String>() {{
            put("username", username);
        }};

        request("GET", builder, mapper.writeValueAsString(body), new TypeReference<Vote>() {
        }, true, callback);

        resetBuilder();
    }

    public String getSignedInUsername() {
        return signedInUsername;
    }
}