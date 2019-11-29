package ir.payebash.data.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ir.payebash.data.RecentVisits;
import ir.payebash.data.User;

/**
 * Created by Alireza Eskandarpour Shoferi on 11/10/2017.
 */

@Database(entities = {User.class, RecentVisits.class/*, Product.class*/}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract ContactDao contactDao();

    public abstract ProductDao productDao();


    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "payebash-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
