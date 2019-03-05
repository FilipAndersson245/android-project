package se.ju.myapplication.Create.MemeTemplate;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import se.ju.myapplication.Connection;
import se.ju.myapplication.Meme;
import se.ju.myapplication.MemeTemplate;
import se.ju.myapplication.MemeViewAdapter;
import se.ju.myapplication.R;
import se.ju.myapplication.Vote;

public class ListMemeTemplateActivity extends Activity {

    private RecyclerView mtRecyclerView;
    private RecyclerView.Adapter mtAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;

    public List<MemeTemplate> mtDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------------------------------------------
        // Ta bort sen
        mtDataset = new ArrayList<MemeTemplate>();

        for (int i = 0; i < 10; i++)
        {
            mtDataset.add(new MemeTemplate(1 , "Foo " + i, "abc", "Bar " + i));
        }
        // ---------------------------------------------

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_template_list);

        mtRecyclerView = (RecyclerView) findViewById(R.id.memeTemplateRecyclerView);

        mtLayoutManager = new LinearLayoutManager(this);
        mtRecyclerView.setLayoutManager(mtLayoutManager);
        mtRecyclerView.setHasFixedSize(true);


        mtAdapter = new ListMemeTemplateAdapter(mtDataset);
        mtRecyclerView.setAdapter(mtAdapter);

        loadMemeTemplates();
    }

    void loadMemeTemplates() {
        Connection.getInstance().getMemeTemplates(null, null, null, null, (memeTemplateResults) -> {

            List<MemeTemplate> memeTemplates = (List<MemeTemplate>) memeTemplateResults;
            Handler mainHandler = new Handler(getBaseContext().getMainLooper());

            // fortsätt här sen

//            final ListView listView = findViewById(R.id.listView);
//
//            Runnable myRunnable = () -> listView.setAdapter(new MemeViewAdapter(memes, getApplicationContext()));
//
//            mainHandler.post(myRunnable);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
