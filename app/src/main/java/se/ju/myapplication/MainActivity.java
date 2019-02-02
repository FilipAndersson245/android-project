package se.ju.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_list);

        ArrayList<Meme> al=new ArrayList<>();
        al.add(new Meme(1,1,"bob", "xX 360 MLG !Xx","wtf.com",360, new Timestamp(1549106319)));
        al.add(new Meme(2,1,"bob2", "xX 360 MLG !Xx","wtf.com",360, new Timestamp(1549106319)));

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new MemeViewAdapter(al,this));

    }
}
