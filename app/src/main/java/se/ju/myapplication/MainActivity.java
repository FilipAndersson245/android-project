package se.ju.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Create.Meme.CreateMemeActivity;
import se.ju.myapplication.Create.MemeTemplate.CreateMemeTemplateActivity;
import se.ju.myapplication.Create.MemeTemplate.ListMemeTemplateActivity;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawer;

    private ArrayList<Fragment> mFragmentStack = new ArrayList<>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        Connection.getInstance().recreateSession(this);

        if (savedInstanceState == null) {
            // Adds first main instance

            Fragment fragment = fragmentFromItemId(R.id.navHome);

            mFragmentStack.add(fragment);

            getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).commit();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mDrawer = findViewById(R.id.drawerLayout);
        NavigationView nvDrawer = findViewById(R.id.nvView);
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
            case R.id.closeDrawerButton:
                break;
            case R.id.navSignOut:
                Connection.getInstance().signOutUser();
                Connection.getInstance().clearSession(this);
                updateUserSignedOut();
                break;
            case R.id.navCreateMeme:
                Intent createMemeIntent = new Intent(this, CreateMemeActivity.class);
                startActivity(createMemeIntent);
                break;
            case R.id.navTemplates:
                Intent templatesIntent = new Intent(this, ListMemeTemplateActivity.class);
                startActivity(templatesIntent);
                break;
            case R.id.navCreateTemplate:
                Intent createTemplateIntent = new Intent(this, CreateMemeTemplateActivity.class);
                startActivity(createTemplateIntent);
                break;
            default:
                Fragment fragment = fragmentFromItemId(itemId);
                if (fragment == null) {
                    // No valid fragment
                    return false;
                }
                replaceFragment(fragment, false);
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void updateUserSignedOut() {
        MainFeedFragment mainFeedFragment = (MainFeedFragment) getStackFragmentFromClassName(MainFeedFragment.class.getName());

        mainFeedFragment.signInVotesUpdater();

        updateDrawerMenu();
    }

    // This code is used to select which item is clicked in the drawer. Done switch case style.
    public Fragment fragmentFromItemId(int itemId) {
        switch (itemId) {
            case R.id.navHome:
                return MainFeedFragment.newInstance();
            case R.id.navRegister:
                return RegisterFragment.newInstance();
            case R.id.navSignIn:
                return SignInFragment.newInstance();
            default:
                return null;
        }
    }

    public Fragment getStackFragmentFromClassName(String fragmentClassName) {
        final Integer fragmentStackIndexFromClassName = getFragmentStackIndexFromClassName(fragmentClassName);
        assert fragmentStackIndexFromClassName != null;
        return mFragmentStack.get(fragmentStackIndexFromClassName);
    }

    private Integer getFragmentStackIndexFromClassName(String fragmentClassName) {
        for (int i = 0; i < mFragmentStack.size(); i++)
            if (mFragmentStack.get(i).getClass().getName().equals(fragmentClassName)) return i;
        return null;
    }

    private void replaceFragment(Fragment newFragment, boolean animate) {
        Integer fragmentIndex;

        fragmentIndex = getFragmentStackIndexFromClassName(newFragment.getClass().getName());

        if (fragmentIndex != null) {
            newFragment = mFragmentStack.get(fragmentIndex);
            mFragmentStack.remove(fragmentIndex.intValue());
        }

        mFragmentStack.add(newFragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (animate) {
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        ft.replace(R.id.flContent, newFragment).commit();
    }

    public void removeAndReplaceWithFragment(int id) {
        mFragmentStack.remove(mFragmentStack.size() - 1);
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
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        Fragment currentFragment = getCurrentFragment();
        boolean isMainFragment = MainFeedFragment.class.getName().equals(currentFragment.getClass().getName());
        if (isMainFragment) {
            finish();
        } else if (mFragmentStack.size() > 1) {
            mFragmentStack.remove(mFragmentStack.size() - 1);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.flContent, mFragmentStack.get(mFragmentStack.size() - 1))
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

        ((TextView) findViewById(R.id.toolbarUsername)).setText(Connection.getInstance().isSignedIn() ? Connection.getInstance().getSignedInUsername() : "");
    }
}