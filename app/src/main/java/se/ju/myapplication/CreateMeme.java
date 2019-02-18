package se.ju.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateMeme extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);

        final Button button = findViewById(R.id.selectTemplateButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Heeeyy it works!");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    public void SelectMemeTemplateClicked() {
//
//    }
}
