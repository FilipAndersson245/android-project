package se.ju.myapplication;

import android.app.ListActivity;
import android.os.Bundle;

public class ListMemeActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_list);
    }
}
