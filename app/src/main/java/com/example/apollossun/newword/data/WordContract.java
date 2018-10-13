package com.example.apollossun.newword.data;

import android.provider.BaseColumns;

public class WordContract implements BaseColumns {
    public static final String TABLE_NAME = "dictionary";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_ISKNOWN = "isknown";
}
