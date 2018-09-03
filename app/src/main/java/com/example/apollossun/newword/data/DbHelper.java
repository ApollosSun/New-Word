package com.example.apollossun.newword.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.apollossun.newword.data.model.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "dictionary.db";
    private static final String LOG_TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + WordContract.TABLE_NAME + " ("
                + WordContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WordContract.COLUMN_WORD + " TEXT, "
                + WordContract.COLUMN_TRANSLATION + " TEXT, "
                + WordContract.COLUMN_COMMENT + " TEXT);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WordContract.TABLE_NAME);
        onCreate(db);
    }

    public long insertWord(String word, String translation, String comment){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(WordContract.COLUMN_WORD, word);
        cv.put(WordContract.COLUMN_TRANSLATION, translation);
        cv.put(WordContract.COLUMN_COMMENT, comment);

        long id = db.insert(WordContract.TABLE_NAME, null, cv);

        db.close();

        return id;
    }

    public Word getSingleWord(long id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                WordContract.TABLE_NAME,
                null,
                WordContract._ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        try {
            cursor.moveToFirst();

            Word word = new Word(
                    cursor.getInt(cursor.getColumnIndex(WordContract._ID)),
                    cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_WORD)),
                    cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_TRANSLATION)),
                    cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_COMMENT))
            );

            return word;

        } catch (NullPointerException e){
            Log.i(LOG_TAG, "Exception: " + e);
        }

        cursor.close();

        return new Word();
    }

    public List<Word> getWords(){

        List<Word> words = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + WordContract.TABLE_NAME
                        + " ORDER BY " + WordContract._ID + " DESC",
                null);

        if(cursor.moveToFirst()){
            do {

                Word word = new Word(
                        cursor.getInt(cursor.getColumnIndex(WordContract._ID)),
                        cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_WORD)),
                        cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_TRANSLATION)),
                        cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_COMMENT))
                );

                words.add(word);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return words;
    }

    public int getWordCount(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + WordContract.TABLE_NAME,
                null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public void updateWord(Word word){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(WordContract.COLUMN_WORD, word.getWord());
        cv.put(WordContract.COLUMN_TRANSLATION, word.getTranslation());
        cv.put(WordContract.COLUMN_COMMENT, word.getComment());

        //Updating row
        db.update(WordContract.TABLE_NAME, cv,WordContract._ID + "=?",
                new String[]{String.valueOf(word.getId())});
        db.close();
    }

    public void deleteWord(List<String> deletingIds){
        String[] ids = new String[deletingIds.size()];
        ids = deletingIds.toArray(ids);
        String idStr = TextUtils.join(",", ids);

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WordContract.TABLE_NAME, WordContract._ID + " IN (" + idStr + ")",
                null);
        db.close();
    }

}
