package se.ju.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            System.out.println("HEJSAN");
            List<Meme> memeList = new Connection().getMemes(null, null, "bobb", null, null);
            new Connection().getMemeTemplates(null, "bobb", null, null);
            new Connection().createUser("testuser2", "1");
            for (Meme meme : memeList) {
                System.out.println(meme.toString());
            }
            System.out.println("HEJSAN2");
        } catch (Exception e) {
            System.out.println("ERROR BIG OUCH");
            e.printStackTrace();
        }
    }
}
