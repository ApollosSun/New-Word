package com.example.apollossun.newword.data.model;

public class Word {

    private long id;
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

    public String getRaw (){
        if(translation.length() > 0){
            return word + " - " + translation;
        } else {
            return word;
        }
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

    public long getId() {
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
