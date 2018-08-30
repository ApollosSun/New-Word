package com.example.apollossun.newword.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apollossun.newword.R;
import com.example.apollossun.newword.data.model.Word;

import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder> {

    private List<Word> wordList;

    public WordsAdapter(List<Word> wordList){
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordsAdapter.ViewHolder holder, int position) {
        Word word = wordList.get(position);
        String wordSrc = word.getWord() + " - " + word.getTranslation();
        holder.word.setText(wordSrc);
        if(word.getComment().length() > 0){
            holder.comment.setText(word.getComment());
        } else {
            holder.comment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView word;
        public TextView comment;

        ViewHolder(View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.tv_word);
            comment = itemView.findViewById(R.id.tv_comment);
        }
    }

}
