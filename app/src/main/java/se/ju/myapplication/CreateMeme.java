package se.ju.myapplication;

import android.app.Activity;
import android.os.Bundle;

public class CreateMeme extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
