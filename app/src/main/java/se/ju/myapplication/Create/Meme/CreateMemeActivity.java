package se.ju.myapplication.Create.Meme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import se.ju.myapplication.Create.MemeTemplate.ListMemeTemplateActivity;
import se.ju.myapplication.R;

public class CreateMemeActivity extends Activity {

    private ImageView templateImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);

        this.templateImage = findViewById(R.id.createMemeTemplateImage);
    }

    public void selectTemplateButtonClicked(View view) {
        Intent intent = new Intent(this, ListMemeTemplateActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == 1) {
                byte[] b = data.getByteArrayExtra("selectedTemplate");
                Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
                templateImage.setImageBitmap(bmp);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
