package se.ju.myapplication.Create.MemeTemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Models.MemeTemplate;
import se.ju.myapplication.R;

public class ListMemeTemplateActivity extends Activity {

    private RecyclerView mtRecyclerView;
    private ListMemeTemplateAdapter mtAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;

    private int pageNumber = 0;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_template_list);

        context = this;

        mtRecyclerView = (RecyclerView) findViewById(R.id.memeTemplateRecyclerView);

        mtLayoutManager = new LinearLayoutManager(context);
        mtRecyclerView.setLayoutManager(mtLayoutManager);
        mtRecyclerView.setHasFixedSize(true);

        Connection.getInstance().getMemeTemplates(null, null, null, pageNumber, (memeTemplateResults) -> {

            ArrayList<MemeTemplate> memeTemplates = (ArrayList<MemeTemplate>) memeTemplateResults;

            if (memeTemplates.size() > 0) {
                Handler mainHandler = new Handler(getBaseContext().getMainLooper());

                Runnable myRunnable = () -> {
                    this.mtAdapter = new ListMemeTemplateAdapter(context, memeTemplates);
                    this.mtAdapter.setOnItemClickListener((templateId, imageSource, templateImage) -> {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("templateId", templateId);
                        returnIntent.putExtra("templateImageSource", imageSource);
                        setResult(1 ,returnIntent);
                        finish();
                    });

                    mtRecyclerView.setAdapter(mtAdapter);
                    pageNumber++;
                };
                mainHandler.post(myRunnable);
            }

        });

        mtRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Connection.getInstance().getMemeTemplates(null, null, null, pageNumber, (memeTemplateResults) -> {

                        ArrayList<MemeTemplate> memeTemplates = (ArrayList<MemeTemplate>) memeTemplateResults;

                        if (memeTemplates.size() > 0){
                            Handler mainHandler = new Handler(getBaseContext().getMainLooper());

                            Runnable myRunnable = () -> {
                                mtAdapter.addTemplatesToShow(memeTemplates);
                                mtAdapter.setOnItemClickListener((templateId, imageSource, templateImage) -> {
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("templateId", templateId);
                                    returnIntent.putExtra("templateImageSource", imageSource);
                                    setResult(1 ,returnIntent);
                                    finish();
                                });
                                mtAdapter.notifyDataSetChanged();
                                pageNumber++;
                            };

                            mainHandler.post(myRunnable);
                        }

                    });
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}