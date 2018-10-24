package com.example.apollossun.newword.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.apollossun.newword.R;
import com.example.apollossun.newword.SettingsActivity;
import com.example.apollossun.newword.data.DbHelper;
import com.example.apollossun.newword.data.model.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LearnFragment extends Fragment {

    private TextView tvWord;
    private TextView tvTranslation;
    private TextView tvComment;
    private RelativeLayout relativeLayout;
    private ToggleButton toggleButton;

    private List<Word> wordList = new ArrayList<>();
    List<Integer> randomList = new ArrayList<>();
    private DbHelper db;

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
        relativeLayout = rootView.findViewById(R.id.word_field);
        toggleButton = rootView.findViewById(R.id.btn_known);

        isWordCompleted = true;

        db = new DbHelper(getActivity());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String wordsToLearnPrefs = preferences.getString("pref_wordsToStudy", "");

        updateWordList(wordsToLearnPrefs);

        //If list is not empty - updating the page with new random word
        updatePage();

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWord();
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    toggleButton.setBackgroundDrawable(getActivity().getResources()
                            .getDrawable(R.drawable.button_shape_on));
                    wordList.remove(word);
                    word.setIsknown(1);
                    db.updateWord(word);
                } else {
                    toggleButton.setBackgroundDrawable(getActivity().getResources()
                            .getDrawable(R.drawable.button_shape_off));
                    word.setIsknown(0);
                    db.updateWord(word);
                }
            }
        });

        return rootView;
    }

    private void updateWordList(String wordsToLearnPrefs) {
        if(wordsToLearnPrefs.equals("Unlearned words")){
            wordList.addAll(db.getUnknownWords());
        } else {
            wordList.addAll(db.getWords());
        }
    }

    private void updatePage(){
        if(wordList.size() == 0){
            relativeLayout.setEnabled(false);
            toggleButton.setVisibility(View.INVISIBLE);
        } else {
            updateWord();
        }
    }

    private void updateWord(){
        getNextWord();

        if(isWordCompleted){
            isWordCompleted = false;

            setToggleButtonState();
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

    private void setToggleButtonState() {
        if(word.getIsknown() == 1){
            toggleButton.setChecked(true);
            toggleButton.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.button_shape_on));
        } else {
            toggleButton.setChecked(false);
            toggleButton.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.button_shape_off));
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
