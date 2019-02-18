package se.ju.myapplication;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements Callback {
    ArrayList<Meme> al = new ArrayList<>();
    Connection connection = new Connection(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_list);

//        try {
//            connection.signInUser("bobb", "abcd1234");
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        connection.getMemes(null, null, null, null, null);
    }

    @Override
    public void call(Object result) {
        if(result.getClass() == Void.class)
        {
            return;
        }

        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

        al = (ArrayList<Meme>) result;

        final ListView listView = findViewById(R.id.listView);

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new MemeViewAdapter(al, getApplicationContext()));
            }
        };

        mainHandler.post(myRunnable);

    }
}
