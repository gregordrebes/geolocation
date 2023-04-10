package com.example.geolocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ReadableDatabaseManager {

    private static SQLiteDatabase instance;

    public static SQLiteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context, "GeoWalking", null, 2).getReadableDatabase();
        }

        return instance;
    }

    private ReadableDatabaseManager() {}
}
