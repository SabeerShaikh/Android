package com.example.assignment.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.assignment.dataModel.AssignmentModel;

import java.util.List;

/**
 * Created by Sabeer Shaikh on 11/28/19.
 */
@Dao
public interface DatabaseInterface {

    @Query("SELECT * FROM assignmentmodel")
    List<AssignmentModel> getAllItems();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(AssignmentModel... testDemoListItems);


}