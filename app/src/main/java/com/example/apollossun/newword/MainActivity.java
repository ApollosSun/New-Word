package com.example.apollossun.newword;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apollossun.newword.data.DbHelper;
import com.example.apollossun.newword.data.model.Word;
import com.example.apollossun.newword.utils.MyDividerItemDecoration;
import com.example.apollossun.newword.utils.RecyclerTouchListener;
import com.example.apollossun.newword.view.WordsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ACCESS_CREATE = 1;
    private static final int REQUEST_ACCESS_UPDATE = 2;

    private WordsAdapter mWordsAdapter;
    private List<Word> wordList = new ArrayList<>();
    private TextView tvNoWords;
    private DbHelper db;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        tvNoWords = findViewById(R.id.tv_empty_list);

        db = new DbHelper(this);

        wordList.addAll(db.getWords());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateWordActivity.class);
                startActivityForResult(intent, REQUEST_ACCESS_CREATE);
            }
        });

        mWordsAdapter = new WordsAdapter(wordList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mWordsAdapter);

        toggleEmptyWords();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /*
     * Receiving word from CreateWordActivity
     * and inserting a new or updating an existing word
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == RESULT_OK){
            String word = data.getStringExtra(CreateWordActivity.WORD_KEY);
            String translation = data.getStringExtra(CreateWordActivity.TRANSLATION_KEY);
            String comment = data.getStringExtra(CreateWordActivity.COMMENT_KEY);

            if(requestCode==REQUEST_ACCESS_CREATE) {
                createWord(word, translation, comment);
            } else if(requestCode==REQUEST_ACCESS_UPDATE){
                updateWord(word, translation, comment, mPosition);
            }else{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /*
     * Inserting new word in db
     * and refreshing the list
     */
    private void createWord(String word, String translation, String comment){
        long id = db.insertWord(word, translation, comment);

        Word w = db.getSingleWord(id);

        if(w != null){
            //Adding new word to array list at 0 position
            wordList.add(0, w);

            //Refreshing the list
            mWordsAdapter.notifyItemInserted(0);

            toggleEmptyWords();
        }
    }

    /*
     * Updating word in db and updating
     * item in the list by its position
     */
    private void updateWord(String word, String translation, String comment, int position){
        Word w = wordList.get(position);
        //Updating word
        w.setWord(word);
        w.setTranslation(translation);
        w.setComment(comment);

        //Updating word in db
        db.updateWord(w);

        //Refreshing the list
        wordList.set(position, w);
        mWordsAdapter.notifyItemChanged(position);

        toggleEmptyWords();
    }

    /*
     * Deleting a word from db
     * and removing item from the list
     */
    private void deleteWord(int position){
        //Deleting the note from db
        db.deleteWord(wordList.get(position));

        //Removing the word from the list
        wordList.remove(position);
        mWordsAdapter.notifyItemRemoved(position);

        toggleEmptyWords();
    }

    /*
     * Shows dialog with Edit&Delete options
     */
    private void showActionsDialog(final int position){
        CharSequence[] items = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            if(which == 0){
                mPosition = position;
                updateWordIntent(wordList.get(position));
            } else {
                deleteWord(position);
            }
            }
        });
        builder.show();
    }

    private void updateWordIntent(final Word w){
        Intent intent = new Intent(MainActivity.this, CreateWordActivity.class);
        intent.putExtra(CreateWordActivity.WORD_KEY, w.getWord());
        intent.putExtra(CreateWordActivity.TRANSLATION_KEY, w.getTranslation());
        intent.putExtra(CreateWordActivity.COMMENT_KEY, w.getComment());
        startActivityForResult(intent, REQUEST_ACCESS_UPDATE);
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyWords() {
        // you can check notesList.size() > 0

        if (db.getWordCount() > 0) {
            tvNoWords.setVisibility(View.GONE);
        } else {
            tvNoWords.setVisibility(View.VISIBLE);
        }
    }

}

/*
     * Shows dialog with Edit options to
     * create/edit a word.
     * When shouldUpdate==true, it automatically displays old word
     * and changes the button text to Update

private void showWordDialog(final boolean shouldUpdate, final Word w, final int position){
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    View view = layoutInflater.inflate(R.layout.word_dialog, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
    alertDialogBuilder.setView(view);

    final EditText inputWord = view.findViewById(R.id.et_word);
    final EditText inputTranslation = view.findViewById(R.id.et_translation);
    final EditText inputComment = view.findViewById(R.id.et_comment);

    TextView dialogTitle = view.findViewById(R.id.tv_word);
    dialogTitle.setText(shouldUpdate ? "Edit word" : "New word");

    if(shouldUpdate && w != null){
        inputWord.setText(w.getWord());
        inputTranslation.setText(w.getTranslation());
        inputComment.setText(w.getComment());
    }

    alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    });

    final AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();

    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Show toast when no text is entered
            if(TextUtils.isEmpty(inputWord.getText().toString())){
                Toast.makeText(MainActivity.this, "Type a word",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                alertDialog.dismiss();
            }

            String word = inputWord.getText().toString();
            String translation = inputTranslation.getText().toString();
            String comment = inputComment.getText().toString();

            if(shouldUpdate && word.length() > 0){
                updateWord(word, translation, comment, position);
            } else {
                createWord(word, translation, comment);
            }
        }
    });
}
 */