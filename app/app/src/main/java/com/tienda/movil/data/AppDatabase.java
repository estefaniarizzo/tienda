package com.tienda.movil.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.tienda.movil.data.Client;
import com.tienda.movil.data.Product;

@Database(entities = { Product.class, Client.class }, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();

    public abstract ClientDao clientDao();
}
