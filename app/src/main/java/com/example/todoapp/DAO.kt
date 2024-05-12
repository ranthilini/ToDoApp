package com.example.todoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao

interface DAO {
    @Insert
    suspend fun insertTask(entity: Entity)

    @Update
    suspend fun updateTask(entity: Entity)

    @Delete
    suspend fun deleteTask(entity: Entity)

    @Query("Delete from 'To_Do'")
    suspend fun deleteAll()

    @Query("Select * from 'To_Do'")
    suspend fun getTasks():List<CardInfo>

    @Query("SELECT * FROM 'To_Do' WHERE title LIKE '%' || :query || '%'")
    suspend fun searchTasks(query: String): List<CardInfo>

}