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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.apollossun.newword.data.WordContract;

public class CreateWordActivity extends AppCompatActivity{

    private static final String LOG_TAG = CreateWordActivity.class.getSimpleName();

    private EditText mWord;
    private EditText mTranslation;
    private EditText mComment;
    private ToggleButton mToggleButton;

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
        mToggleButton = findViewById(R.id.btn_known);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setEditText(extras);
            getSupportActionBar().setTitle("Edit the word");
        } else {
            mToggleButton.setChecked(false);
        }

        setToggleButtonState();
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mToggleButton.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.button_shape_on));
                } else {
                    mToggleButton.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.button_shape_off));
                }
            }
        });
    }

    private void setToggleButtonState() {
        if(mToggleButton.isChecked()){
            mToggleButton.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.button_shape_on));
        } else {
            mToggleButton.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.button_shape_off));
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
            int isknown = mToggleButton.isChecked() ? 1 : 0;
            submitWord(word, translation, comment, isknown);
        }
    }

    private void submitWord(String word, String translation, String comment, int  isknown){

        Intent intent = new Intent();
        intent.putExtra(WordContract.COLUMN_WORD, word);
        intent.putExtra(WordContract.COLUMN_TRANSLATION, translation);
        intent.putExtra(WordContract.COLUMN_COMMENT, comment);
        intent.putExtra(WordContract.COLUMN_ISKNOWN, isknown);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void setEditText(Bundle extras){
        mWord.setText(extras.getString(WordContract.COLUMN_WORD));
        mTranslation.setText(extras.getString(WordContract.COLUMN_TRANSLATION));
        mComment.setText(extras.getString(WordContract.COLUMN_COMMENT));
        mToggleButton.setChecked(extras.getInt(WordContract.COLUMN_ISKNOWN) == 1);
    }

}