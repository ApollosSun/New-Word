package com.example.apollossun.newword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.apollossun.newword.data.DbHelper;

public class StatisticsActivity extends AppCompatActivity {

    private static final String LOG_TAG = StatisticsActivity.class.getSimpleName();

    private TextView mTotal;
    private TextView mLearned;
    private TextView mUnlearned;

    private DbHelper db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e){
            Log.i(LOG_TAG, "Exception" + e);
        }

        db = new DbHelper(this);

        mTotal = findViewById(R.id.total_words);
        mLearned = findViewById(R.id.learned_words);
        mUnlearned = findViewById(R.id.unlearned_words);

        updateStatistic();
    }

    private void updateStatistic() {

        int total = db.getWordCount();
        int learned = db.getUnknownWordCount();
        int unlearned = total - learned;

        String totalStr = "Total words - " + total;
        String learnedStr = "Learned words - " + unlearned;
        String unlearnedStr = "Unlearned words - " + learned;

        mTotal.setText(totalStr);
        mLearned.setText(learnedStr);
        mUnlearned.setText(unlearnedStr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
