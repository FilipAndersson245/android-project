package se.ju.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Connection conn = new Connection(this);
            conn.getMemes(null, null, "bobb", null, null);
//            conn.createUser("k","k");
        } catch (Exception e) {
            System.out.println("ERROR BIG OUCH");
            e.printStackTrace();
        }
    }

    @Override
    public void call(Object result) {
        for (Meme meme : (List<Meme>) result) {
            System.out.println("hej");
            System.out.println(meme.getUsername());
        }
    }
}
