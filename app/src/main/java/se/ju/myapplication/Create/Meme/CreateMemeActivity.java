package se.ju.myapplication.Create.Meme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.picasso.Picasso;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Create.MemeTemplate.ListMemeTemplateActivity;
import se.ju.myapplication.R;

public class CreateMemeActivity extends Activity {

    private ImageView templateImage;
    private LinearLayout layout;

    private int templateId;
    private String templateImageSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);

        this.layout = findViewById(R.id.editNewMemeFrame);

        for (int i = 0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setEnabled(false);
        }

        this.templateImage = findViewById(R.id.createMemeTemplateImage);

        findViewById(R.id.selectTemplateButton).setOnClickListener((View v) -> {
            selectTemplateButtonClicked();
        });

        findViewById(R.id.createMemeButton).setOnClickListener((View v) -> {
            createMemeButtonClicked();
        });
    }

    public void selectTemplateButtonClicked() {
        Intent intent = new Intent(this, ListMemeTemplateActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == 1) {
                this.templateId = data.getIntExtra("templateId", 0);
                this.templateImageSource = data.getStringExtra("templateImageSource");


                Picasso.get()
                        .load(templateImageSource)
                        .placeholder(R.drawable.spinner)
                        .into(templateImage);

                for (int i = 0; i < layout.getChildCount(); i++) {
                    layout.getChildAt(i).setEnabled(true);
                }
            }
        }
    }

    public void createMemeButtonClicked() {
        findViewById(R.id.layoutProgressBar).setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        String topText = ((EditText) findViewById(R.id.topTextEdit)).getText().toString();
        String bottomText = ((EditText) findViewById(R.id.bottomTextEdit)).getText().toString();
        String title = ((EditText) findViewById(R.id.memeTitleEdit)).getText().toString();

        try {
            Connection.getInstance().createMeme(templateId, Connection.getInstance().getSignedInUsername(), title, this.templateImageSource, topText, bottomText, callback -> {
                new Handler(getBaseContext().getMainLooper()).post(() -> {
                    findViewById(R.id.layoutProgressBar).setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                });
            });
            this.finish();
        } catch (JsonProcessingException e) {
            Toast.makeText(this, R.string.meme_unable_to_create, Toast.LENGTH_SHORT).show();
            findViewById(R.id.layoutProgressBar).setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
