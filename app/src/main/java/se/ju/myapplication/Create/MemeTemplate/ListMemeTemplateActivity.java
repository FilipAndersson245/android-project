package se.ju.myapplication.Create.MemeTemplate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import se.ju.myapplication.Connection;
import se.ju.myapplication.MemeTemplate;
import se.ju.myapplication.R;

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

            Runnable myRunnable = () -> {
                this.mtAdapter = new ListMemeTemplateAdapter(this, memeTemplates);
                this.mtAdapter.setOnItemClickListener(new ListMemeTemplateAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, Drawable templateImage) {
                        Bitmap bitmap = ((BitmapDrawable)templateImage).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] b = baos.toByteArray();

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("selectedTemplate", b);
                        setResult(1 ,returnIntent);
                        finish();
                    }
                });

                mtRecyclerView.setAdapter(mtAdapter);
            };

            mainHandler.post(myRunnable);

            pageNumber++;
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}