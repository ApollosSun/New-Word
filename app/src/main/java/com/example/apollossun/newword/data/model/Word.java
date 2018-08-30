package com.example.apollossun.newword.data.model;

public class Word {

    private int id;
    private String word;
    private String translation;
    private String comment;

    public Word(){

    }

    public Word(int id, String word, String translation, String comment){
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.comment = comment;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

    public String getComment() {
        return comment;
    }

}
