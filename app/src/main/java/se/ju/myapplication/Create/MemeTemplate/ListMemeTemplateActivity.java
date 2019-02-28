package se.ju.myapplication.Create.MemeTemplate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import se.ju.myapplication.MemeTemplate;
import se.ju.myapplication.R;

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
            mtDataset.add(new MemeTemplate("Foo " + i, "Bar " + i));
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
