package com.nevexo.proiectseminar.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nevexo.proiectseminar.Model.Masina;

import java.util.List;

@Dao
public interface MasinaDAO {

    @Insert
    void insert(Masina masina);

    @Insert
    void insertAll(List<Masina> masini);

    @Query("SELECT * FROM masini")
    List<Masina> getAll();

    @Delete
    void delete(Masina masina);

    @Query("DELETE FROM masini")
    void deleteAll();

    @Update
    void update(Masina masina);
}
