package se.ju.myapplication;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.util.Consumer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Connection connection = new Connection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_list);

        try {
            Connection.getInstance().signInUser("bobb2", "abcd1234", (voidObject) -> {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        connection.getMemes(null, null, null, null, null, (memesResult) -> {
            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;
            Handler mainHandler = new Handler(getBaseContext().getMainLooper());

            System.out.println("2");

            for(Meme meme : memes)
            {
                try {
                    connection.getVote(meme.getId(), connection.getSignedInUsername(), (voteResult) -> {
                        Vote vote = (Vote) voteResult;
                        meme.setVote(vote.getVote());
                    });
                } catch (JsonProcessingException e) {
                    continue;
                }
            }

            final ListView listView = findViewById(R.id.listView);

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(new MemeViewAdapter(memes, getApplicationContext()));
                }
            };

            mainHandler.post(myRunnable);
        });
    }

}
