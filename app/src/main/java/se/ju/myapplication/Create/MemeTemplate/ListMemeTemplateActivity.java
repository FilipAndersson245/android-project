package se.ju.myapplication.Create.MemeTemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Models.MemeTemplate;
import se.ju.myapplication.R;

public class ListMemeTemplateActivity extends Activity {

    private RecyclerView mRecyclerView;
    private ListMemeTemplateAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mPageNumber = 0;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_template_list);

        mContext = this;

        mRecyclerView = findViewById(R.id.memeTemplateRecyclerView);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        Connection.getInstance().getMemeTemplates(null, null, null, mPageNumber, (memeTemplateResults) -> {

            ArrayList<MemeTemplate> memeTemplates = (ArrayList<MemeTemplate>) memeTemplateResults;

            if (memeTemplates.size() > 0) {
                Handler mainHandler = new Handler(getBaseContext().getMainLooper());

                Runnable myRunnable = () -> {
                    this.mAdapter = new ListMemeTemplateAdapter(mContext, memeTemplates);
                    this.mAdapter.setOnItemClickListener((templateId, imageSource, templateImage) -> {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("templateId", templateId);
                        returnIntent.putExtra("templateImageSource", imageSource);
                        setResult(1 ,returnIntent);
                        finish();
                    });

                    mRecyclerView.setAdapter(mAdapter);
                    mPageNumber++;
                };
                mainHandler.post(myRunnable);
            }

        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Connection.getInstance().getMemeTemplates(null, null, null, mPageNumber, (memeTemplateResults) -> {

                        ArrayList<MemeTemplate> memeTemplates = (ArrayList<MemeTemplate>) memeTemplateResults;

                        if (memeTemplates.size() > 0){
                            Handler mainHandler = new Handler(getBaseContext().getMainLooper());

                            Runnable myRunnable = () -> {
                                mAdapter.addTemplatesToShow(memeTemplates);
                                mAdapter.setOnItemClickListener((templateId, imageSource, templateImage) -> {
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("templateId", templateId);
                                    returnIntent.putExtra("templateImageSource", imageSource);
                                    setResult(1 ,returnIntent);
                                    finish();
                                });
                                mAdapter.notifyDataSetChanged();
                                mPageNumber++;
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