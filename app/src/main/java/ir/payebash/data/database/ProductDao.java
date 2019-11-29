package ir.payebash.data.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi on 11/10/2017.
 */

@Dao
public interface ProductDao {

   /* @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplace(Product product);

    @Query("SELECT * FROM product WHERE id=:id")
    Product fetchOrder(long id);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM product")
    List<Product> fetchOrders();

    @Query("DELETE FROM product")
    void deleteAll();*/
}
