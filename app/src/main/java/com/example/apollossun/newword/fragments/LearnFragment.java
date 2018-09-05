package com.example.apollossun.newword.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apollossun.newword.R;
import com.example.apollossun.newword.data.DbHelper;
import com.example.apollossun.newword.data.model.Word;

import java.util.ArrayList;
import java.util.List;

public class LearnFragment extends Fragment {

    TextView tvWord;
    TextView tvTranslation;
    TextView tvComment;

    LinearLayout linearLayout;
    private List<Word> wordList = new ArrayList<>();
    private DbHelper db;

    private boolean isWordCompleted;

    private int i = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);

        tvWord = rootView.findViewById(R.id.tv_word);
        tvTranslation = rootView.findViewById(R.id.tv_translation);
        tvComment = rootView.findViewById(R.id.tv_comment);

        isWordCompleted = true;

        db = new DbHelper(getActivity());
        //TODO handle updating
        wordList.addAll(db.getWords());

        updateWord();

        linearLayout = rootView.findViewById(R.id.word_field);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWord();
            }
        });

        return rootView;
    }

    private void updateWord(){

        Word word = wordList.get(i);

        if(isWordCompleted){
            tvWord.setText(word.getWord());
            tvComment.setText(word.getComment());
            tvTranslation.setText("");
            isWordCompleted = false;
        } else {
            tvTranslation.setText(word.getTranslation());
            isWordCompleted = true;

            if(i == wordList.size()-1){
                i = 0;
            } else {
                i++;
            }
        }

    }

}
