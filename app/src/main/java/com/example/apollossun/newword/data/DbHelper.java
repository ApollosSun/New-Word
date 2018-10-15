package com.example.apollossun.newword.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.apollossun.newword.data.model.Word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "dictionary.db";
    private static String DB_PATH;
    private static final String LOG_TAG = DbHelper.class.getSimpleName();

    private Context mContext;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        createDb();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String query = "CREATE TABLE " + WordContract.TABLE_NAME + " ("
//                + WordContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + WordContract.COLUMN_WORD + " TEXT, "
//                + WordContract.COLUMN_TRANSLATION + " TEXT, "
//                + WordContract.COLUMN_COMMENT + " TEXT);";
//
//        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE dictionary ADD COLUMN isknown INTEGER DEFAULT 0");
        }
    }

    public void createDb(){
        InputStream inputStream;
        OutputStream outputStream;
        try{
            String outFileName = DB_PATH + DB_NAME;
            File file = new File(outFileName);
            if(!file.exists()){

                this.getReadableDatabase();
                this.close();

                inputStream = mContext.getAssets().open(DB_NAME);
                outputStream = new FileOutputStream(outFileName);
                int length;
                byte[] buffer = new byte[1024];

                while((length = inputStream.read(buffer)) > 0){
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        } catch(IOException ex){
            Log.d("DatabaseHelper", ex.getMessage());
        }
    }

    public long insertWord(String word, String translation, String comment, int isknown){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(WordContract.COLUMN_WORD, word);
        cv.put(WordContract.COLUMN_TRANSLATION, translation);
        cv.put(WordContract.COLUMN_COMMENT, comment);
        cv.put(WordContract.COLUMN_ISKNOWN, isknown);

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
                    cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_COMMENT)),
                    cursor.getInt(cursor.getColumnIndex(WordContract.COLUMN_ISKNOWN))
            );

            return word;

        } catch (NullPointerException e){
            Log.i(LOG_TAG, "Exception: " + e);
        }

        cursor.close();
        db.close();

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
                        cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_COMMENT)),
                        cursor.getInt(cursor.getColumnIndex(WordContract.COLUMN_ISKNOWN))
                );

                words.add(word);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return words;
    }

    public List<Word> getUnknownWords(){

        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + WordContract.TABLE_NAME
                        + " WHERE " + WordContract.COLUMN_ISKNOWN + " = 0"
                        + " ORDER BY " + WordContract._ID + " DESC",
                null);

        if(cursor.moveToFirst()){
            do {

                Word word = new Word(
                        cursor.getInt(cursor.getColumnIndex(WordContract._ID)),
                        cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_WORD)),
                        cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_TRANSLATION)),
                        cursor.getString(cursor.getColumnIndex(WordContract.COLUMN_COMMENT)),
                        cursor.getInt(cursor.getColumnIndex(WordContract.COLUMN_ISKNOWN))
                );

                words.add(word);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return words;
    }

    public int getWordCount(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + WordContract.TABLE_NAME,
                null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    public int getUnknownWordCount(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + WordContract.TABLE_NAME
                        + " WHERE " + WordContract.COLUMN_ISKNOWN + " = 0",
                null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    public void updateWord(Word word){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(WordContract.COLUMN_WORD, word.getWord());
        cv.put(WordContract.COLUMN_TRANSLATION, word.getTranslation());
        cv.put(WordContract.COLUMN_COMMENT, word.getComment());
        cv.put(WordContract.COLUMN_ISKNOWN, word.getIsknown());

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
