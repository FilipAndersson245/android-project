package se.ju.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

        ArrayList<Meme> al=new ArrayList<>();
        al.add(new Meme(1,1,"bob", "PLG #mirror","https://schpoopstorage.s3.eu-central-1.amazonaws.com/memes/1895d1ff-f021-44a0-b814-02f4e951c4f0",5, new Timestamp(1549106319)));
        al.add(new Meme(2,1,"bob2", "swag","https://schpoopstorage.s3.eu-central-1.amazonaws.com/memes/0a07f11c-602a-4b00-a6c3-b54da107a413",32, new Timestamp(1549106336)));
        al.add(new Meme(2,1,"bob2", "Ow is dead","https://schpoopstorage.s3.eu-central-1.amazonaws.com/memes/807eccc2-16ce-4c77-9c25-6176421d8a0c",64, new Timestamp(1549106324)));



        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new MemeViewAdapter(al,this));

    }
}
