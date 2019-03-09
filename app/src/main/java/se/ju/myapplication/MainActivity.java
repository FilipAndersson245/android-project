package se.ju.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.app.Activity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);


        if (savedInstanceState == null) {
            // Adds first main instance

            Fragment fragment = fragmentFromItemId(R.id.nav_home);

            String tag = fragment.getClass().getName();
            int fragmentHolderLayoutId = R.id.flContent;

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction ();

            ft.add ( fragmentHolderLayoutId, fragment, tag );
            ft.commit ();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        nvDrawer.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        dismissKeyboard();

        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Integer itemId = menuItem.getItemId();

        if(itemId != R.id.close_drawer_button) {
            replaceFragment(fragmentFromItemId(itemId), this);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }

    // This code is used to select which item is clicked in the drawer. Done switch case style.
    private Fragment fragmentFromItemId(int itemId){
        switch (itemId) {
            case R.id.nav_home:
                return MainFeedFragment.newInstance();
            // Remake this to a fragment pls!!
            //            case R.id.nav_create_meme:
            //                Intent k = new Intent(MainActivity.this, CreateMemeActivity.class);
            //                startActivity(k);
            //                return true;
            case R.id.nav_sign_in:
                return SignInFragment.newInstance();
            default:
                System.out.println("No handler was found for drawer item!");
                return null;
        }
    }

    private void replaceFragment( Fragment fragment, Context context ) {
        String tag = fragment.getClass().getName();
        int fragmentHolderLayoutId = R.id.flContent;

        FragmentManager manager = ( (AppCompatActivity) context ).getSupportFragmentManager ();
        manager.findFragmentByTag ( tag );
        FragmentTransaction ft = manager.beginTransaction ();

        if (manager.findFragmentByTag ( tag ) == null) { // No fragment in backStack with same tag..
            ft.replace ( fragmentHolderLayoutId, fragment, tag );
            ft.addToBackStack ( tag );
            ft.commit ();
        }
        else {
            ft.replace(fragmentHolderLayoutId, manager.findFragmentByTag( tag )).commit();
        }
    }

    public void dismissKeyboard() {
        Activity activity = this;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            FragmentManager manager = getSupportFragmentManager();
            int fragments = manager.getBackStackEntryCount();
            boolean isMainFragment = manager.findFragmentByTag(MainFeedFragment.class.getName()).isVisible();
            if (isMainFragment) {
                finish();
            } else if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }
}