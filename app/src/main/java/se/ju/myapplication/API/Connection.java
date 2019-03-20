package se.ju.myapplication.API;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri.Builder;
import android.support.v4.util.Consumer;

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

import static android.content.Context.MODE_PRIVATE;

public class Connection {
    private static final String TOKEN_ID = "JWT_TOKEN";
    private static final String USERNAME_ID = "USERNAME";

    private static final Connection ourInstance = new Connection();

    private final ObjectMapper mMapper = new ObjectMapper();
    private String mJsonWebToken;
    private String mSignedInUsername;

    public String signInError;
    public String registerError;

    private Connection() {
    }

    public static Connection getInstance() {
        return ourInstance;
    }

    private Builder newBuilder() {
        return new Builder().scheme("http").authority("schpoop.eu-central-1.elasticbeanstalk.com");
    }

    public void recreateSession(Activity context) {
        final SharedPreferences preferences = context.getPreferences(MODE_PRIVATE);
        String token = preferences.getString(TOKEN_ID, null);
        String username = preferences.getString(USERNAME_ID, null);
        if (token != null && username != null) {
            this.mJsonWebToken = token;
            this.mSignedInUsername = username;
        }
    }

    public void setSession(Activity context) {
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TOKEN_ID, this.mJsonWebToken);
        editor.putString(USERNAME_ID, this.mSignedInUsername);
        editor.apply();
    }

    public void clearSession(Activity context) {
        SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(TOKEN_ID);
        editor.remove(USERNAME_ID);
        editor.apply();
    }

    private void request(final String method, final Builder urlBuilder, final String body, final TypeReference returnType, final boolean authorization, final Consumer<Object> callback) {
        new Thread(() -> {
            try {
                URL url = new URL(urlBuilder.build().toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod(method);

                if (authorization) {
                    String bearerToken = "Bearer " + mJsonWebToken;
                    conn.setRequestProperty("Authorization", bearerToken);
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
                        callback.accept(mMapper.readValue(conn.getInputStream(), returnType));
                    }
                } else {
                    if (resultRange == 400) {
                        throw new Exception(mMapper.readValue(conn.getErrorStream(), Error.class).getError());
                    }
                    throw new Exception("Error making request.");
                }
            } catch (Exception e) {
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
                os.write(mMapper.writeValueAsString(user).getBytes(StandardCharsets.UTF_8));
                os.close();

                int resultRange = (conn.getResponseCode() / 100) * 100;

                if (resultRange == 200) {
                    callback.accept(true);
                } else {
                    if (resultRange == 400) {
                        throw new Exception(mMapper.readValue(conn.getErrorStream(), Error.class).getError());
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

    public void signInUser(final String username, String password, Consumer<Boolean> callback) {
        Builder builder = newBuilder().appendPath("sessions");

        final Session session = new Session("password", username, password);

        new Thread(() -> {
            try {
                URL url = new URL(builder.build().toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();
                os.write(mMapper.writeValueAsString(session).getBytes(StandardCharsets.UTF_8));
                os.close();

                int resultRange = (conn.getResponseCode() / 100) * 100;

                if (resultRange == 200) {
                    mJsonWebToken = mMapper.readValue(conn.getInputStream(), SessionResponse.class).getAccessToken();
                    mSignedInUsername = username;
                    callback.accept(true);
                } else {
                    mJsonWebToken = null;
                    if (resultRange == 400) {
                        throw new Exception(mMapper.readValue(conn.getErrorStream(), Error.class).getError());
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
        mJsonWebToken = null;
        mSignedInUsername = null;
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

        request("POST", builder, mMapper.writeValueAsString(newMeme), new TypeReference<Void>() {
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
                if (name != null && name != "") {
                    params.put("name", name);
                }

                MemeTemplate result = new MultipartRequest().multipartRequest(builder.build().toString(), params, image.getAbsolutePath(), "image", mJsonWebToken);

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

    // ============================== VOTES =============================
    // ==================================================================

    public void vote(Integer memeId, String username, Integer vote, Consumer<Object> callback) throws JsonProcessingException {
        Builder builder = newBuilder().appendPath("votes");
        builder.appendPath(memeId.toString());

        NewVote newVote = new NewVote(username, vote);

        request("PUT", builder, mMapper.writeValueAsString(newVote), new TypeReference<Vote>() {
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

        request("DELETE", builder, mMapper.writeValueAsString(body), new TypeReference<Void>() {
        }, true, callback);
    }


    public String getSignedInUsername() {
        return mSignedInUsername;
    }

    public Boolean isSignedIn() {
        return mSignedInUsername != null;
    }
}