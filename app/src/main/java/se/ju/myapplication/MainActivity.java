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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_list);

        try {
            Connection.getInstance().signInUser("bob123", "bigboybanana1337", (voidObject) -> {
                System.out.println(" ============================ HURRAY, LOGGED IN");
                loadMemes();
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    void testGetVote() {
        try {
            Connection.getInstance().getVote(1, Connection.getInstance().getSignedInUsername(), (returnVote) -> {
                Vote vote = (Vote) returnVote;
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA the vote is:");
                System.out.println(vote.getVote());
            });
        } catch (JsonProcessingException e) {
            System.out.println("BBBBBBBBBBBBBBBBBBBBB står bög");
            e.printStackTrace();
        }
    }

    void loadMemes() {
        Connection.getInstance().getMemes(null, null, null, null, null, (memesResult) -> {
            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;
            Handler mainHandler = new Handler(getBaseContext().getMainLooper());

            for (Meme meme : memes) {
                try {
                    Connection.getInstance().getVote(meme.getId(), Connection.getInstance().getSignedInUsername(), (voteResult) -> {
                        Vote vote = (Vote) voteResult;
                        meme.setVote(vote.getVote());
                    });
                } catch (JsonProcessingException e) {
                    meme.setVote(0);
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
