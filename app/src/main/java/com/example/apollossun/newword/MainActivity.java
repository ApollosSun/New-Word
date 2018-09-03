package com.example.apollossun.newword;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.apollossun.newword.data.DbHelper;
import com.example.apollossun.newword.data.model.Word;
import com.example.apollossun.newword.utils.MyDividerItemDecoration;
import com.example.apollossun.newword.utils.RecyclerTouchListener;
import com.example.apollossun.newword.view.WordsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActionMode.Callback{

    private static final int REQUEST_ACCESS_CREATE = 1;
    private static final int REQUEST_ACCESS_UPDATE = 2;

    private WordsAdapter wordsAdapter;
    private RecyclerView recyclerView;

    private List<Word> wordList = new ArrayList<>();
    private List<String> selectedIds = new ArrayList<>();
    private List<Integer> selectedPositions = new ArrayList<>();
    private List<Word> selectedWords = new ArrayList<>();

    private TextView tvNoWords;
    private DbHelper db;
    private ActionMode actionMode;

    private int mPosition;
    private boolean isMultiSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
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

        wordsAdapter = new WordsAdapter(this, wordList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(wordsAdapter);

        toggleEmptyWords();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(isMultiSelect){
                    multiSelect(position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if(!isMultiSelect){
                    isMultiSelect = true;

                    if(actionMode == null){
                        actionMode = startSupportActionMode(MainActivity.this);
                    }
                }
                multiSelect(position);
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
            wordsAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);

            toggleEmptyWords();
        }
    }

    /*
     * Updating word in db and updating
     * item in the list by its position
     */
    private void updateWord(String word, String translation, String comment, int position){
        Word w = wordList.get(position);

        w.setWord(word);
        w.setTranslation(translation);
        w.setComment(comment);

        //Updating word in db
        db.updateWord(w);

        //Refreshing the list
        wordList.set(position, w);
        wordsAdapter.notifyItemChanged(position);

        toggleEmptyWords();
    }

    /*
     * Deleting a word from db
     * and removing item from the list
     */
    private void deleteWord(){
        //Deleting words from db
        db.deleteWord(selectedIds);

        for (int position : selectedPositions){
            selectedWords.add(wordList.get(position));
        }

        //Removing words from the list
        wordList.removeAll(selectedWords);
        wordsAdapter.notifyDataSetChanged();

        selectedPositions.clear();
        selectedWords.clear();
        toggleEmptyWords();
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

    private void multiSelect(int position){

        long id = wordList.get(position).getId();

        if(actionMode != null){

            if(selectedIds.contains(String.valueOf(id))){
                selectedIds.remove(String.valueOf(id));
                selectedPositions.remove(Integer.valueOf(position));
            } else {
                selectedIds.add(String.valueOf(id));
                selectedPositions.add(position);
            }

            if(selectedIds.size() > 1){
                actionMode.getMenu().getItem(0).setVisible(false);
            } else if(selectedIds.size() == 1) {
                actionMode.getMenu().getItem(0).setVisible(true);
            }

            if(selectedIds.size() > 0){
                //Show amount of selected items on actionMode
                actionMode.setTitle(String.valueOf(selectedIds.size()));
            } else {
                actionMode.setTitle("");
                actionMode.finish();
            }

            wordsAdapter.setSelectedIds(selectedIds, position);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_select, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_action:
                mPosition = selectedPositions.get(0);
                updateWordIntent(wordList.get(mPosition));
                actionMode.finish();
                return true;
            case R.id.delete_action:
                deleteWord();
                actionMode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        isMultiSelect = false;
        selectedIds.clear();
        wordsAdapter.setSelectedIds(selectedIds);
        selectedPositions.clear();
        selectedWords.clear();
    }

}