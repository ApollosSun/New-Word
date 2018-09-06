package com.example.apollossun.newword.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apollossun.newword.R;
import com.example.apollossun.newword.data.DbHelper;
import com.example.apollossun.newword.data.model.Word;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LearnFragment extends Fragment {

    private TextView tvWord;
    private TextView tvTranslation;
    private TextView tvComment;
    private LinearLayout linearLayout;

    private List<Word> wordList = new ArrayList<>();
    List<Integer> randomList = new ArrayList<>();

    Word word;

    private boolean isWordCompleted;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);

        tvWord = rootView.findViewById(R.id.tv_word);
        tvTranslation = rootView.findViewById(R.id.tv_translation);
        tvComment = rootView.findViewById(R.id.tv_comment);
        linearLayout = rootView.findViewById(R.id.word_field);

        isWordCompleted = true;

        DbHelper db = new DbHelper(getActivity());
        wordList.addAll(db.getWords());

        //If list is not empty - updating the page with new random word
        updatePage();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWord();
            }
        });

        return rootView;
    }

    private void updatePage(){
        if(wordList.size() == 0){
            linearLayout.setEnabled(false);
        } else {
            updateWord();
        }
    }

    private void updateWord(){
        getNextWord();

        if(isWordCompleted){
            isWordCompleted = false;
            tvWord.setText(word.getWord());

            String comment = word.getComment();
            if(comment.isEmpty()){
                tvComment.setVisibility(View.GONE);
            } else {
                tvComment.setVisibility(View.VISIBLE);
                tvComment.setText(word.getComment());
            }
            tvTranslation.setVisibility(View.INVISIBLE);
        } else {
            isWordCompleted = true;
            String translation = word.getTranslation();
            if(translation.isEmpty()){
                updateWord();
            } else {
                tvTranslation.setVisibility(View.VISIBLE);
                tvTranslation.setText(translation);
            }
        }
    }

    private void getNextWord(){
        if(randomList.size() == 0 && isWordCompleted){
            randomList = getRandomList();
            word = wordList.get(randomList.remove(0));
        } else if(isWordCompleted){
            word = wordList.get(randomList.remove(0));
        }
    }

    private List<Integer> getRandomList(){

        int size = wordList.size();

        Random random = new Random();

        List<Integer> randomList = new ArrayList<>(size);
        List<Integer> helperList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            helperList.add(i);
        }

        while (helperList.size() > 0){
            int i = random.nextInt(helperList.size());
            randomList.add(helperList.remove(i));
        }

        return randomList;
    }

}
