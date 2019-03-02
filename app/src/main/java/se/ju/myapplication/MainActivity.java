package se.ju.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.util.Consumer;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import se.ju.myapplication.Create.Meme.CreateMemeActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        try {
            Connection.getInstance().signInUser("bob123", "bigboybanana1337", (voidObject) -> {
                System.out.println(" ============================ HURRAY, LOGGED IN");
                loadMemes();
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        nvDrawer.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This code is used to select which item is clicked in the drawer. Done switch case style.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        System.out.println(menuItem.getTitle());

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_create_meme:
                try {
                    Intent k = new Intent(MainActivity.this, CreateMemeActivity.class);
                    startActivity(k);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        return false;
    }

    void loadMemes() {
        Connection.getInstance().getMemes(null, null, null, null, null, (memesResult) -> {
            ArrayList<Meme> memes = (ArrayList<Meme>) memesResult;
            Handler mainHandler = new Handler(getBaseContext().getMainLooper());

            for (Meme meme : memes) {
                try {
                    Connection.getInstance().getVote(meme.getId(), Connection.getInstance().getSignedInUsername(), (voteResult) -> {
                        Vote vote = (Vote) voteResult;
                        meme.setVote(vote.getVote());
                    });
                } catch (JsonProcessingException e) {
                    meme.setVote(0);
                }
            }

            final ListView listView = findViewById(R.id.listView);

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(new MemeViewAdapter(memes, getApplicationContext()));
                }
            };

            mainHandler.post(myRunnable);
        });
    }
}