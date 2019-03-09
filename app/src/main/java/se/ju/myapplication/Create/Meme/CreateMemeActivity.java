package se.ju.myapplication.Create.Meme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import se.ju.myapplication.Create.MemeTemplate.ListMemeTemplateActivity;
import se.ju.myapplication.R;

public class CreateMemeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);
    }

    public void selectTemplateButtonClicked(View view) {
        Intent intent = new Intent(this, ListMemeTemplateActivity.class);
//        startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == 1) {
                Bitmap selected = (Bitmap) data.getExtras().get("selectedTemplate");
                setMemeTemplateImage(selected);
            }
        }
    }

    public void setMemeTemplateImage(Bitmap selectedImage) {
        ImageView image = findViewById(R.id.memeTemplateImage);
        image.setImageBitmap(selectedImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
