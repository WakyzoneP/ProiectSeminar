package com.nevexo.proiectseminar.database;

import static androidx.room.Room.databaseBuilder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.nevexo.proiectseminar.Model.Masina;

@Database(entities = Masina.class, version = 1, exportSchema = false)
@TypeConverters({   DateConverter.class})
public abstract class MasinaDB extends RoomDatabase {

    public static final String DB_NAME = "masini.db";

    public static MasinaDB instance;

    public static MasinaDB getInstance(Context context) {
        if(instance == null) {
            instance = databaseBuilder(context, MasinaDB.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract MasinaDAO getMasinaDAO();
}
