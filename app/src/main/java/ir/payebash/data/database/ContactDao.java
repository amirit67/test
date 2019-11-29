package ir.payebash.data.database;


import androidx.room.Dao;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi on 11/10/2017.
 */

@Dao
public interface ContactDao {
    /*@Query("SELECT * FROM contact")
    List<Contact> fetchContacts();

    @Insert
    void insertNewContact(Contact contact);

    @Update
    void editContact(Contact user);

    @Query("SELECT * FROM contact WHERE id=:id")
    Contact fetchContact(long id);

    @Query("DELETE FROM contact")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewContacts(List<Contact> contacts);

    @Delete
    void deleteContacts(Contact... contacts);*/
}
