package com.example.apollossun.newword;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class CreateWordActivity extends AppCompatActivity{

    public static final String WORD_KEY = "word";
    public static final String TRANSLATION_KEY = "translation";
    public static final String COMMENT_KEY = "comment";
    private static final String LOG_TAG = CreateWordActivity.class.getSimpleName();

    private EditText mWord;
    private EditText mTranslation;
    private EditText mComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_word);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e){
            Log.i(LOG_TAG, "Exception" + e);
        }

        mWord = findViewById(R.id.et_word);
        mTranslation = findViewById(R.id.et_translation);
        mComment = findViewById(R.id.et_comment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setEditText(extras);
            getSupportActionBar().setTitle("Edit the word");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_word, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_action:
                createWordInstance();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createWordInstance(){

        if(TextUtils.isEmpty(mWord.getText().toString())) {
            Toast.makeText(this, "Type a word", Toast.LENGTH_SHORT).show();
        } else {
            String word = mWord.getText().toString().trim();
            String translation = mTranslation.getText().toString().trim();
            String comment = mComment.getText().toString().trim();
            submitWord(word, translation, comment);
        }
    }

    private void submitWord(String word, String translation, String comment){

        Intent intent = new Intent();
        intent.putExtra(WORD_KEY, word);
        intent.putExtra(TRANSLATION_KEY, translation);
        intent.putExtra(COMMENT_KEY, comment);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void setEditText(Bundle extras){
        mWord.setText(extras.getString(WORD_KEY));
        mTranslation.setText(extras.getString(TRANSLATION_KEY));
        mComment.setText(extras.getString(COMMENT_KEY));
    }

}