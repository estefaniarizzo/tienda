package com.tienda.movil.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Client {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String email;
    public String phone;
}
