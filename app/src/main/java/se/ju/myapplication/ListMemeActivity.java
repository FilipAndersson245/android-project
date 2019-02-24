package se.ju.myapplication;

import android.app.Activity;
import android.os.Bundle;

public class ListMemeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_template_list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
