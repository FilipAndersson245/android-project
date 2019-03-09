package se.ju.myapplication.Create.MemeTemplate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileInputStream;
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

            Runnable myRunnable = () -> mtRecyclerView.setAdapter(new ListMemeTemplateAdapter(this.getApplicationContext(), memeTemplates));

            mainHandler.post(myRunnable);

            pageNumber++;
        });


        BroadcastReceiver selectedTemplateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent selectedTemplateIntent) {
//                Bitmap selectedTemplate = null;
//                String filename = getIntent().getStringExtra("image");
//                try {
//                    FileInputStream is = openFileInput(filename);
//                    selectedTemplate = BitmapFactory.decodeStream(is);
//                    is.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                byte[] byteArray = selectedTemplateIntent.getByteArrayExtra("image");
//                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


                Intent returnIntent = new Intent(selectedTemplateIntent);
//                returnIntent.putExtra("selectedTemplate",selectedTemplate);
//                setResult(1, returnIntent);

                finish();
            }
        };
    }






    public void templateSelected(Bitmap selectedTemplate){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("selectedTemplate",selectedTemplate);
        setResult(1,returnIntent);
        finish();
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