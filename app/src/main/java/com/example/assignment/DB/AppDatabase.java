package com.example.assignment.DB;

import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.assignment.dataModel.AssignmentModel;

@Database(entities = {AssignmentModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DatabaseInterface databaseInterface();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}