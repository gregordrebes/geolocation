package com.geolocatepoop;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table routes(id integer primary key autoincrement, name varchar not null, description text, coordinates text not null, created_at DATE DEFAULT (datetime('now','localtime')));");
        db.execSQL("create table categories(id integer primary key autoincrement, name varchar not null);");
        db.execSQL("insert into categories (name) values ('Lixo')");
        db.execSQL("insert into categories (name) values ('Cocô')");
        db.execSQL("insert into categories (name) values ('Bituca')");
        db.execSQL("insert into categories (name) values ('Outros')");
        db.execSQL("create table alerts(id integer primary key autoincrement, id_route integer, id_category integer, name varchar not null, description text, coordinates text not null, created_at DATE DEFAULT (datetime('now','localtime')), photo_64 text, active boolean default 1 not null, FOREIGN KEY (id_route) REFERENCES routes (id), FOREIGN KEY (id_category) REFERENCES categories (id_category));");
//        db.execSQL("create table coordinates(id integer primary key autoincrement, route_id int, coordinates varchar not null, created_at DATE DEFAULT (datetime('now','localtime')));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
