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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.Activity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    private ArrayList<Fragment> fragmentStack = new ArrayList<Fragment>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);


        if (savedInstanceState == null) {
            // Adds first main instance

            Fragment fragment = fragmentFromItemId(R.id.nav_home);

            fragmentStack.add(fragment);

            getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).commit();
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

        switch (itemId) {
            case R.id.close_drawer_button:
                break;
            case R.id.nav_sign_out:
                Connection.getInstance().signOutUser();
                updateUserSignedState();
                break;
            default:
                Fragment fragment = fragmentFromItemId(itemId);
                if (fragment == null)
                    return false;
                replaceFragment(fragment, false);
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void updateUserSignedState() {
        MainFeedFragment mainFeedFragment = (MainFeedFragment) getStackFragmentFromClassName(MainFeedFragment.class.getName());
        mainFeedFragment.updateList();
        updateDrawerMenu();
    }

    // This code is used to select which item is clicked in the drawer. Done switch case style.
    public static Fragment fragmentFromItemId(int itemId) {
        switch (itemId) {
            case R.id.nav_home:
                return MainFeedFragment.newInstance();
            case R.id.nav_register:
                return RegisterFragment.newInstance();
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

    public Fragment getStackFragmentFromClassName(String fragmentClassName) {
        final Integer fragmentStackIndexFromClassName = getFragmentStackIndexFromClassName(fragmentClassName);
        assert fragmentStackIndexFromClassName != null;
        return fragmentStack.get(fragmentStackIndexFromClassName);
    }

    private Integer getFragmentStackIndexFromClassName(String fragmentClassName) {
        for (int i = 0; i < fragmentStack.size(); i++)
            if (fragmentStack.get(i).getClass().getName() == fragmentClassName) return i;
        return null;
    }

    private void replaceFragment(Fragment newFragment, boolean animate) {
        Integer fragmentIndex;

        fragmentIndex = getFragmentStackIndexFromClassName(newFragment.getClass().getName());

        if (fragmentIndex != null) {
            newFragment = fragmentStack.get(fragmentIndex);
            fragmentStack.remove(fragmentIndex.intValue());
        }

        fragmentStack.add(newFragment);
        if (animate)
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                    .replace(R.id.flContent, newFragment)
                    .commit();
        else
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContent, newFragment)
                    .commit();
    }

    public void removeAndReplaceWithFragment(int id) {
        fragmentStack.remove(fragmentStack.size() - 1);
        replaceFragment(fragmentFromItemId(id), true);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        Fragment currentFragment = getCurrentFragment();
        boolean isMainFragment = MainFeedFragment.class.getName() == currentFragment.getClass().getName();
        if (isMainFragment) {
            finish();
        } else if (fragmentStack.size() > 1) {
            fragmentStack.remove(fragmentStack.size() - 1);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.flContent, fragmentStack.get(fragmentStack.size() - 1))
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.flContent);
    }

    public void updateDrawerMenu() {
        NavigationView navView = findViewById(R.id.nvView);
        navView.getMenu().clear();
        navView.inflateMenu(Connection.getInstance().isSignedIn() ? R.menu.drawer_view_signed_in : R.menu.drawer_view);
    }
}