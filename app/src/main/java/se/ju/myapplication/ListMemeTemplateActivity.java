package se.ju.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ListMemeTemplateActivity extends Activity {

    private RecyclerView mtRecyclerView;
    private RecyclerView.Adapter mtAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;

    private List<MemeTemplate> mtDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // -------------------------------------------
        for (int i = 0; i < 10; i++)
            mtDataset.add(new MemeTemplate("Foo" + i, "Bar" + i));
        // ---------------------------------------------

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme_template);

        mtRecyclerView = (RecyclerView) findViewById(R.id.memeTemplateRecyclerView);

        mtRecyclerView.setHasFixedSize(true);

        mtLayoutManager = new LinearLayoutManager(this);
        mtRecyclerView.setLayoutManager(mtLayoutManager);

        mtAdapter = new ListMemeTemplateAdapter(mtDataset);
        mtRecyclerView.setAdapter(mtAdapter);

    }

}
