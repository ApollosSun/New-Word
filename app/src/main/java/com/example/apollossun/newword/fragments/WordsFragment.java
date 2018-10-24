package com.example.apollossun.newword.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apollossun.newword.CreateWordActivity;
import com.example.apollossun.newword.MainActivity;
import com.example.apollossun.newword.R;
import com.example.apollossun.newword.data.DbHelper;
import com.example.apollossun.newword.data.WordContract;
import com.example.apollossun.newword.data.model.Word;
import com.example.apollossun.newword.utils.MyDividerItemDecoration;
import com.example.apollossun.newword.utils.RecyclerTouchListener;
import com.example.apollossun.newword.view.WordsAdapter;

import java.util.ArrayList;
import java.util.List;

public class WordsFragment extends Fragment implements ActionMode.Callback{

    private static final int REQUEST_ACCESS_CREATE = 1;
    private static final int REQUEST_ACCESS_UPDATE = 2;

    private WordsAdapter wordsAdapter;
    private RecyclerView recyclerView;

    private List<Word> wordList = new ArrayList<>();
    private List<Word> fullWordList;
    private List<String> selectedIds = new ArrayList<>();
    private List<Integer> selectedPositions = new ArrayList<>();
    private List<Word> selectedWords = new ArrayList<>();

    private TextView tvNoWords;
    private DbHelper db;
    private ActionMode actionMode;

    private int mPosition;
    private boolean isMultiSelect = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_words, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        tvNoWords = rootView.findViewById(R.id.tv_empty_list);

        db = new DbHelper(getActivity());
        wordList.addAll(db.getWords());
        fullWordList = new ArrayList<>(wordList);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateWordActivity.class);
                startActivityForResult(intent, REQUEST_ACCESS_CREATE);
            }
        });

        wordsAdapter = new WordsAdapter(getActivity(), wordList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL, 8));
        recyclerView.setAdapter(wordsAdapter);

        toggleEmptyWords();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
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
                        actionMode = getActivity().startActionMode(WordsFragment.this);
                    }
                }
                multiSelect(position);
            }
        }));

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /*
     * Receiving word from CreateWordActivity
     * and inserting a new or updating an existing word
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == MainActivity.RESULT_OK){
            String word = data.getStringExtra(WordContract.COLUMN_WORD);
            String translation = data.getStringExtra(WordContract.COLUMN_TRANSLATION);
            String comment = data.getStringExtra(WordContract.COLUMN_COMMENT);
            int isknown = data.getIntExtra(WordContract.COLUMN_ISKNOWN, 0);

            if(requestCode==REQUEST_ACCESS_CREATE) {
                createWord(word, translation, comment, isknown);
            } else if(requestCode==REQUEST_ACCESS_UPDATE){
                updateWord(word, translation, comment, isknown, mPosition);
            }else{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /*
     * Inserting new word in db
     * and refreshing the list
     */
    private void createWord(String word, String translation, String comment, int isknown){
        long id = db.insertWord(word, translation, comment, isknown);

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
    private void updateWord(String word, String translation, String comment, int isknown, int position){
        Word w = wordList.get(position);

        w.setWord(word);
        w.setTranslation(translation);
        w.setComment(comment);
        w.setIsknown(isknown);

        //Updating word in db
        db.updateWord(w);

        //Refreshing the list
        wordList.set(position, w);
        wordsAdapter.notifyItemChanged(position);
    }

    private void updateWordState(int wordState){
        for (int position : selectedPositions){

            Word w = wordList.get(position);
            w.setIsknown(wordState);

            db.updateWord(w);

            wordList.set(position, w);
            wordsAdapter.notifyItemChanged(position);
        }
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
        Intent intent = new Intent(getActivity(), CreateWordActivity.class);
        intent.putExtra(WordContract.COLUMN_WORD, w.getWord());
        intent.putExtra(WordContract.COLUMN_TRANSLATION, w.getTranslation());
        intent.putExtra(WordContract.COLUMN_COMMENT, w.getComment());
        intent.putExtra(WordContract.COLUMN_ISKNOWN, w.getIsknown());
        startActivityForResult(intent, REQUEST_ACCESS_UPDATE);
    }

    /**
     * Toggling list and empty words view
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
            case R.id.mark_learned:
                updateWordState(1);
                actionMode.finish();
                return true;
            case R.id.mark_not_learned:
                updateWordState(0);
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

    @Override
    public void onStop() {
        if(actionMode != null){
            actionMode.finish();
        }
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String input = newText.toLowerCase();
                wordList.clear();
                for (Word searchedWord : fullWordList){
                    if(searchedWord.getWord().toLowerCase().contains(input)){
                        wordList.add(searchedWord);
                    }
                }
                wordsAdapter.search(wordList);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}
