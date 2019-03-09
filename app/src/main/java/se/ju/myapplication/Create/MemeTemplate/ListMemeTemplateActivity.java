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
    private ListMemeTemplateAdapter mtAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;

    private int pageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_template_list);

        mtRecyclerView = (RecyclerView) findViewById(R.id.memeTemplateRecyclerView);

        mtLayoutManager = new LinearLayoutManager(this);
        mtRecyclerView.setLayoutManager(mtLayoutManager);
        mtRecyclerView.setHasFixedSize(true);

        Connection.getInstance().getMemeTemplates(null, null, null, pageNumber, (memeTemplateResults) -> {

            ArrayList<MemeTemplate> memeTemplates = (ArrayList<MemeTemplate>) memeTemplateResults;

            Handler mainHandler = new Handler(getBaseContext().getMainLooper());

            Runnable myRunnable = () -> mtRecyclerView.setAdapter(new ListMemeTemplateAdapter(memeTemplates));

            mainHandler.post(myRunnable);

            pageNumber++;
        });
    }



//    void loadMemeTemplates() {
//        Connection.getInstance().getMemeTemplates(null, null, null, pageNumber, (memeTemplateResults) -> {
//
//            ArrayList<MemeTemplate> memeTemplates = (ArrayList<MemeTemplate>) memeTemplateResults;
//
//            Handler mainHandler = new Handler(getBaseContext().getMainLooper());
//
//            Runnable myRunnable = () -> mtAdapter.addTemplatesToShow(memeTemplates);
//
//            mainHandler.post(myRunnable);
//        });
//
//        pageNumber++;
//        mtAdapter.notifyDataSetChanged();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}