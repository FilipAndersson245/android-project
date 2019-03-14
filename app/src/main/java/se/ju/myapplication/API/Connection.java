package se.ju.myapplication.API;

import android.media.Image;
import android.net.Uri.Builder;
import android.support.v4.util.Consumer;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.ju.myapplication.Models.Error;
import se.ju.myapplication.Models.Meme;
import se.ju.myapplication.Models.MemeTemplate;
import se.ju.myapplication.Models.NewMeme;
import se.ju.myapplication.Models.NewVote;
import se.ju.myapplication.Models.Session;
import se.ju.myapplication.Models.SessionResponse;
import se.ju.myapplication.Models.User;
import se.ju.myapplication.Models.Vote;

public class Connection {
    private static final Connection ourInstance = new Connection();

    public static Connection getInstance() {
        return ourInstance;
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private String JWT = null;
    private String signedInUsername = null;

    public String signInError = null;
    public String registerError = null;

    Connection() {
    }

    private Builder newBuilder() {
        return new Builder().scheme("http").authority("schpoop.eu-central-1.elasticbeanstalk.com");
    }

    private void request(final String method, final Builder urlBuilder, final String body, final TypeReference returnType, final boolean authorization, final Consumer<Object> callback) {
        new Thread(() -> {
            try {
                URL url = new URL(urlBuilder.build().toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod(method);

                if (authorization) {
                    String a = "Bearer " + JWT;
                    conn.setRequestProperty("Authorization", a);
                }

                if (body != null) {
                    OutputStream os = conn.getOutputStream();
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }

                int resultRange = (conn.getResponseCode() / 100) * 100;

                if (resultRange == 200) {
                    if (returnType.getType() == Void.class) {
                        callback.accept(null);
                    } else {
                        callback.accept(mapper.readValue(conn.getInputStream(), returnType));
                    }
                } else {
                    if (resultRange == 400) {
                        throw new Exception(mapper.readValue(conn.getErrorStream(), Error.class).getError());
                    }
                    throw new Exception("Error making request.");
                }
            } catch (Exception e) {
                System.out.println(" XXXXXXXXXXXXXXXX Failed while doing a " + method + " request on " + urlBuilder.build().toString());
                e.printStackTrace();
            }
        }).start();
    }

    // ============================== USER ==============================
    // ==================================================================

    public void createUser(String username, String password, Consumer<Boolean> callback) {
        Builder builder = newBuilder().appendPath("users");
        User user = new User(username, password);

        new Thread(() -> {
            try {
                URL url = new URL(builder.build().toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();
                os.write(mapper.writeValueAsString(user).getBytes(StandardCharsets.UTF_8));
                os.close();

                StringBuilder sb = new StringBuilder();
                int resultRange = (conn.getResponseCode() / 100) * 100;

                if (resultRange == 200) {
                    callback.accept(true);
                } else {
                    if (resultRange == 400) {
                        throw new Exception(mapper.readValue(conn.getErrorStream(), Error.class).getError());
                    }
                    throw new Exception("Error making request.");
                }
            } catch (Exception e) {
                e.printStackTrace();

                registerError = e.getMessage();

                callback.accept(false);
            }
        }).start();
    }

    public void getUsers(String username, Integer pageSize, Integer page, Consumer<Object> callback) {
        Builder builder = newBuilder().appendPath("users");

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
    }

    public void updateUserPassword(String username, final String newPassword, Consumer<Object> callback) throws JsonProcessingException {
        Builder builder = newBuilder().appendPath("users");
        builder.appendPath(username);

        HashMap<String, String> body = new HashMap<String, String>() {{
            put("password", newPassword);
        }};

        request("PATCH", builder, mapper.writeValueAsString(body), new TypeReference<Void>() {
        }, true, callback);
    }

    public void deleteUser(String username, Consumer<Object> callback) {
        Builder builder = newBuilder().appendPath("users");
        builder.appendPath(username);

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true, callback);
    }

    public void signInUser(final String username, String password, Consumer<Boolean> callback) {
        Builder builder = newBuilder().appendPath("sessions");

        final Session session = new Session("password", username, password);


        new Thread(() -> {
            try {
                System.out.println("START");
                System.out.println(builder.build().toString());
                URL url = new URL(builder.build().toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();
                os.write(mapper.writeValueAsString(session).getBytes(StandardCharsets.UTF_8));
                os.close();

                StringBuilder sb = new StringBuilder();
                int resultRange = (conn.getResponseCode() / 100) * 100;

                if (resultRange == 200) {
                    JWT = mapper.readValue(conn.getInputStream(), SessionResponse.class).getAccessToken();
                    signedInUsername = username;
                    callback.accept(true);
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

                signInError = e.getMessage();

                callback.accept(false);
            }
        }).start();
    }

    public void signOutUser() {
        JWT = null;
        signedInUsername = null;
    }

    // ============================== MEMES =============================
    // ==================================================================

    public void createMeme(Integer templateId, String username, String name, String imageSource, String topText, String bottomText, Consumer<Object> callback) throws JsonProcessingException {
        Builder builder = newBuilder().appendPath("memes");

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
    }

    public void getMemes(String name, Integer templateId, String username, Integer pageSize, Integer page, Consumer<Object> callback) {
        Builder builder = newBuilder().appendPath("memes");

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
    }

    public void getMeme(Integer memeId, Consumer<Object> callback) {
        Builder builder = newBuilder().appendPath("memes");
        builder.appendPath(memeId.toString());

        request("GET", builder, null, new TypeReference<Meme>() {
        }, false, callback);
    }

    public void deleteMeme(Integer memeId, Consumer<Object> callback) {
        Builder builder = newBuilder().appendPath("memes");
        builder.appendPath(memeId.toString());

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true, callback);
    }

    // ========================== MEMETEMPLATES =========================
    // ==================================================================

    public void createMemeTemplate(final String name, final String username, final File image, final Consumer<Object> callback) throws JsonProcessingException {
        Builder builder = newBuilder().appendPath("memetemplates");

        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<String, String>(2);
                params.put("username", username);
                if (name != null && name == "") {
                    params.put("name", name);
                }

                MemeTemplate result = new MultipartRequest().multipartRequest(builder.build().toString(), params, image.getAbsolutePath(), "image", JWT);

                callback.accept(result);

            } catch (Exception e) {
                callback.accept(e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public void getMemeTemplates(String name, String username, Integer pageSize, Integer page, Consumer<Object> callback) {
        Builder builder = newBuilder().appendPath("memetemplates");

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
    }

    public void getMemeTemplate(Integer templateId, Consumer<Object> callback) {
        Builder builder = newBuilder().appendPath("memetemplates");
        builder.appendPath(templateId.toString());

        request("GET", builder, null, new TypeReference<MemeTemplate>() {
        }, false, callback);
    }

    public void deleteMemeTemplate(Integer templateId, Consumer<Object> callback) {
        Builder builder = newBuilder().appendPath("memetemplates");
        builder.appendPath(templateId.toString());

        request("DELETE", builder, null, new TypeReference<Void>() {
        }, true, callback);
    }

    // ============================== VOTES =============================
    // ==================================================================

    public void vote(Integer memeId, String username, Integer vote, Consumer<Object> callback) throws JsonProcessingException {
        Builder builder = newBuilder().appendPath("votes");
        builder.appendPath(memeId.toString());

        NewVote newVote = new NewVote(username, vote);

        System.out.println(builder.toString());

        request("PUT", builder, mapper.writeValueAsString(newVote), new TypeReference<Vote>() {
        }, true, callback);
    }

    public void getVote(Integer memeId, final String username, Consumer<Object> callback) throws JsonProcessingException {
        Builder builder = newBuilder().appendPath("votes");
        builder.appendPath(memeId.toString());
        builder.appendQueryParameter("username", username);

        request("GET", builder, null, new TypeReference<Vote>() {
        }, false, callback);
    }

    public void removeVote(Integer memeId, final String username, Consumer<Object> callback) throws JsonProcessingException {
        Builder builder = newBuilder().appendPath("votes");
        builder.appendPath(memeId.toString());

        HashMap<String, String> body = new HashMap<String, String>() {{
            put("username", username);
        }};

        request("DELETE", builder, mapper.writeValueAsString(body), new TypeReference<Void>() {
        }, true, callback);
    }


    public String getSignedInUsername() {
        return signedInUsername;
    }

    public Boolean isSignedIn() {
        return signedInUsername != null;
    }


    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}