package com.example.apollossun.newword;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.apollossun.newword.fragments.CollectionsFragment;
import com.example.apollossun.newword.fragments.LearnFragment;
import com.example.apollossun.newword.fragments.WordsFragment;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadFragment(new WordsFragment());

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        final BottomNavigationView bnv = findViewById(R.id.navigation_view);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment;

                switch (item.getItemId()){
                    case R.id.navigation_words:
                        fragment = new WordsFragment();
                        loadFragment(fragment);
                        toolbar.setTitle("Words");
                        return true;
                    case R.id.navigation_learn:
                        fragment = new LearnFragment();
                        loadFragment(fragment);
                        toolbar.setTitle("Learn");
                        return true;
                    case R.id.navigation_collections:
                        fragment = new CollectionsFragment();
                        loadFragment(fragment);
                        toolbar.setTitle("Collections");
                        return true;
                }
                return false;
            }
        });

    }

    private void disableCheckedNavigationItem(BottomNavigationView bnv){
        Menu menu = bnv.getMenu();
        for(int i = 0; i < menu.size(); i++){
            if(menu.getItem(i).isChecked()){
                menu.getItem(i).setEnabled(false);
                Log.i("LOG_MAIN", "Checked item is - " + i);
            } else {
                menu.getItem(i).setEnabled(true);
            }
        }
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.statistics:
                Intent intentStat = new Intent(this, StatisticsActivity.class);
                startActivity(intentStat);
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}