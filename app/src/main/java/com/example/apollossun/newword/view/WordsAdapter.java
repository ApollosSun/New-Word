package com.example.apollossun.newword.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.apollossun.newword.R;
import com.example.apollossun.newword.data.model.Word;

import java.util.ArrayList;
import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder> {

    private Context context;
    private List<Word> wordList;
    private List<Long> selectedIds = new ArrayList<>();

    public WordsAdapter(Context context, List<Word> wordList){
        this.context = context;
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

        holder.word.setText(word.getRaw());

        if(word.getComment().length() > 0){
            holder.comment.setText(word.getComment());
        } else {
            holder.comment.setVisibility(View.GONE);
        }

        long id = word.getId();

        if(selectedIds.contains(id)){
            //if item is selected then,set foreground color of FrameLayout.
            holder.rootView.setBackground(new ColorDrawable(ContextCompat.getColor(context,
                    R.color.selection_gray)));
        } else {
            //else remove selected item color.
            holder.rootView.setBackground(new ColorDrawable(ContextCompat.getColor(context,
                    android.R.color.transparent)));
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public void setSelectedIds (List<Long> selectedIds, int position){
        this.selectedIds = selectedIds;
        notifyItemChanged(position);
    }

    public void setSelectedIds (List<Long> selectedIds){
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView word;
        public TextView comment;
        public RelativeLayout rootView;

        ViewHolder(View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.tv_word);
            comment = itemView.findViewById(R.id.tv_comment);
            rootView = itemView.findViewById(R.id.word_row_layout);
        }
    }

}
