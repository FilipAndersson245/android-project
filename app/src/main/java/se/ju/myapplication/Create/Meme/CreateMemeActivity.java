package se.ju.myapplication.Create.Meme;

import android.app.Activity;
import android.content.Intent;
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

    private static final int TEMPLATE_SELECTION = 1;
    private static final int TEMPLATE_PICKED_SUCCESS = 1;

    private ImageView mTemplateImage;
    private LinearLayout mLayout;
    private int mTemplateId;
    private String mTemplateImageSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);

        this.mLayout = findViewById(R.id.editNewMemeFrame);

        for (int i = 0; i < mLayout.getChildCount(); i++) {
            mLayout.getChildAt(i).setEnabled(false);
        }

        this.mTemplateImage = findViewById(R.id.createMemeTemplateImage);

        findViewById(R.id.selectTemplateButton).setOnClickListener((View view) -> selectTemplateButtonClicked());

        findViewById(R.id.createMemeButton).setOnClickListener((View view) -> createMemeButtonClicked());
    }

    public void selectTemplateButtonClicked() {
        Intent intent = new Intent(this, ListMemeTemplateActivity.class);
        startActivityForResult(intent, TEMPLATE_SELECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TEMPLATE_SELECTION && resultCode == TEMPLATE_PICKED_SUCCESS) {
            this.mTemplateId = data.getIntExtra("templateId", 0);
            this.mTemplateImageSource = data.getStringExtra("templateImageSource");

            Picasso.get()
                    .load(mTemplateImageSource)
                    .placeholder(R.drawable.spinner)
                    .into(mTemplateImage);

            for (int i = 0; i < mLayout.getChildCount(); i++) {
                mLayout.getChildAt(i).setEnabled(true);
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
            Connection.getInstance()
                    .createMeme(mTemplateId,
                            Connection.getInstance()
                                    .getSignedInUsername(),
                            title,
                            this.mTemplateImageSource,
                            topText,
                            bottomText,
                            callback -> {
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
