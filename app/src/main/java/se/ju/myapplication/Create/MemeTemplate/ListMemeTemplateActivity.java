package se.ju.myapplication.Create.MemeTemplate;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;


import se.ju.myapplication.Connection;
import se.ju.myapplication.Meme;
import se.ju.myapplication.MemeTemplate;
import se.ju.myapplication.MemeViewAdapter;
import se.ju.myapplication.R;
import se.ju.myapplication.Vote;

public class ListMemeTemplateActivity extends Activity {

    private RecyclerView mtRecyclerView;
    private ListMemeTemplateAdapter mtAdapter;

    private Integer pageNumber = 0;
    private ArrayList<MemeTemplate> memeTemplates = new ArrayList<MemeTemplate>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_template_list);

        mtRecyclerView = (RecyclerView) findViewById(R.id.memeTemplateRecyclerView);
        mtRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mtRecyclerView.setHasFixedSize(true);

        fetchMemeTemplates();

        System.out.println("######### after " + memeTemplates.size());

        mtAdapter = new ListMemeTemplateAdapter(memeTemplates);
        mtRecyclerView.setAdapter(mtAdapter);
    }

    // -------------------------------------------
    // KOLLA UPP CALLBACK ELLER CONSUMER
    // -------------------------------------------


    private void fetchMemeTemplates() {
        Connection.getInstance().getMemeTemplates(null, null, null, null, (memeTemplateResults) -> {

            System.out.println("######### PRE " + ((ArrayList<MemeTemplate>) memeTemplateResults).size());

            memeTemplates = ((ArrayList<MemeTemplate>) memeTemplateResults);

            System.out.println("######### Before " + memeTemplates.size());
        });
        pageNumber++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
