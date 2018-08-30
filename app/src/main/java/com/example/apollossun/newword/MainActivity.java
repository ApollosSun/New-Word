package com.example.apollossun.newword;

import android.content.DialogInterface;
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

    private WordsAdapter mWordsAdapter;
    private List<Word> wordList = new ArrayList<>();
    private TextView tvNoWords;
    private DbHelper db;


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
                showWordDialog(false, null, -1);
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
                    showWordDialog(true, wordList.get(position), position);
                } else {
                    deleteWord(position);
                }
            }
        });
        builder.show();
    }

    /*
     * Shows dialog with Edit options to
     * create/edit a word.
     * When shouldUpdate==true, it automatically displays old word
     * and changes the button text to Update
     */
    private void showWordDialog(final boolean shouldUpdate, final Word w, final int position){
        //TODO try to use this instead of getAppContext
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
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
