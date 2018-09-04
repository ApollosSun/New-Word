package com.example.apollossun.newword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
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

        BottomNavigationView bnv = findViewById(R.id.navigation_view);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment;
                //TODO implement disabling of checked section
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

    private void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

}