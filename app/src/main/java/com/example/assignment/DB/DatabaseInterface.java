package com.example.assignment.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.assignment.dataModel.AssignmentModel;

import java.util.List;

@Dao
public interface DatabaseInterface {

    @Query("SELECT * FROM assignmentmodel")
    List<AssignmentModel> getAllItems();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(AssignmentModel... testDemoListItems);


}