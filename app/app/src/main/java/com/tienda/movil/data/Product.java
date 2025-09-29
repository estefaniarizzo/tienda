package com.tienda.movil.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public double price;
    public String imageUri;
}
