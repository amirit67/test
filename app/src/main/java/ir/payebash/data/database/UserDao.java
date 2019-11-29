package ir.payebash.data.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import ir.payebash.data.User;

/**
 * Created by Alireza Eskandarpour Shoferi on 11/10/2017.
 */

@Dao
public interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    User fetchUser();

    @Update
    void editUserDetails(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("DELETE FROM user")
    void deleteAll();
}
