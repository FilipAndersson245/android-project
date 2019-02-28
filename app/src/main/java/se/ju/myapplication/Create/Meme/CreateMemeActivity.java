package se.ju.myapplication.Create.Meme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import se.ju.myapplication.Create.MemeTemplate.ListMemeTemplateActivity;
import se.ju.myapplication.R;

public class CreateMemeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void selectTemplateButtonClicked(View view) {
        Intent intent = new Intent(this, ListMemeTemplateActivity.class);
        startActivity(intent);
    }

}
