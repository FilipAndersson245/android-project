package se.ju.myapplication;

import android.net.Uri.Builder;

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

interface Callback {
    void call(Object result);
}

public class Connection {
    private final Builder builder = new Builder();
    private final ObjectMapper mapper = new ObjectMapper();
    private Callback callbackNotify;
    private String JWT = null;

    Connection(Callback callback) {
        builder.scheme("http").authority("schpoop.eu-central-1.elasticbeanstalk.com");
        this.callbackNotify = callback;
    }

    private void request(final String method, final Builder urlBuilder, final String body, final TypeReference returnType, final boolean authorization) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlBuilder.build().toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestMethod(method);

                    if (authorization) {
                        conn.setRequestProperty("Authorization", JWT);
                    }

                    if (body != null) {
                        OutputStream os = conn.getOutputStream();
                        os.write(body.getBytes("UTF-8"));
                        os.close();
                    }

                    int resultRange = (conn.getResponseCode() / 100) * 100;

                    if (resultRange == 200) {
                        callbackNotify.call(mapper.readValue(conn.getInputStream(), returnType));
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

    public void createUser(String username, String password) throws JsonProcessingException {
        builder.appendPath("users");
        User user = new User(username, password);

        request("POST", builder, mapper.writeValueAsString(user), new TypeReference<Void>() {
        });
    }

    public void getUsers(String username, Integer pageSize, Integer page) {
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
        }, false);
    }

    public void updateUserPassword(String username, final String newPassword) throws JsonProcessingException {
        builder.appendPath("users");
        builder.appendPath(username);

        HashMap<String, String> body = new HashMap<String, String>() {{
            put("password", newPassword);
        }};

        request("PATCH", builder, mapper.writeValueAsString(body), new TypeReference<Void>() {
        }, );
    }

    public void deleteUser(String username) throws JsonProcessingException {
        builder.appendPath("users");
        builder.appendPath(username);

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true);
    }

    public void signInUser(String username, String password) throws JsonProcessingException {
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
                        JWT = mapper.readValue(conn.getInputStream(), SessionResponse.class).getAccessToken();
                        callbackNotify.call(mapper.readValue(conn.getInputStream(), Void.class));
                    } else {
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
    }

    // ============================== MEMES =============================
    // ==================================================================

    public void createMeme(Integer templateId, String username, String name, String imageSource, String topText, String bottomText) throws JsonProcessingException {
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
        }, true);
    }

    public void getMemes(String name, Integer templateId, String username, Integer pageSize, Integer page) {
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
        }, false);
    }

    public void getMeme(Integer memeId) {
        builder.appendPath("memes");
        builder.appendPath(memeId.toString());

        request("GET", builder, null, new TypeReference<Meme>() {
        }, false);
    }

    public void deleteMeme(Integer memeId) {
        builder.appendPath("memes");
        builder.appendPath(memeId.toString());

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true);
    }

    // ========================== MEMETEMPLATES =========================
    // ==================================================================

    public void createMemeTemplate(final String name, final String username, Integer pageSize, Integer page) throws MalformedURLException, ExecutionException, InterruptedException {
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
        }, false);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MultipartUtility multipart = new MultipartUtility(builder.build().toString(), "UTF-8", callbackNotify);
                    multipart.addFormField("username", username);
                    if (name != null) {
                        multipart.addFormField("name", name);
                    }
                    multipart.addFilePart("image", new File("https://i.imgur.com/zNU9fe8.png"));
                    multipart.addHeaderField("Authorization", JWT);

                    multipart.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getMemeTemplates(String name, String username, Integer pageSize, Integer page) throws MalformedURLException, ExecutionException, InterruptedException {
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
        }, false);
    }

    public void getMemeTemplate(Integer templateId) {
        builder.appendPath("memetemplates");
        builder.appendPath(templateId.toString());

        request("GET", builder, null, new TypeReference<MemeTemplate>() {
        }, false);
    }

    public void deleteMemeTemplate(Integer templateId) {
        builder.appendPath("memetemplates");
        builder.appendPath(templateId.toString());

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true);
    }

    // ============================== VOTES =============================
    // ==================================================================

    public void vote(Integer memeId, String username, Integer vote) throws JsonProcessingException {
        builder.appendPath("votes");
        builder.appendPath(memeId.toString());

        NewVote newVote = new NewVote(username, vote);

        request("PUT", builder, mapper.writeValueAsString(newVote), new TypeReference<Vote>() {
        }, true);
    }

    public void getVote(Integer memeId, final String username) throws JsonProcessingException {
        builder.appendPath("votes");
        builder.appendPath(memeId.toString());

        HashMap<String, String> body = new HashMap<String, String>() {{ put("username", username); }};

        request("GET", builder, mapper.writeValueAsString(body), new TypeReference<Vote>() {
        }, false);
    }

    public void removeVote(Integer memeId, String username) throws JsonProcessingException {
        builder.appendPath("votes");
        builder.appendPath(memeId.toString());

        HashMap<String, String> body = new HashMap<String, String>() {{ put("username", username); }};

        request("GET", builder, mapper.writeValueAsString(body), new TypeReference<Vote>() {
        }, true);
    }
}