package com.example.assignment.dataModel;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Sabeer Shaikh on 11/28/19.
 */
@Entity
public class AssignmentModel {
    /* @PrimaryKey(autoGenerate = true)
     private int id;*/
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "imageHref")
    private String imageHref;

    public AssignmentModel(String title, String description, String imageHref) {
        this.title = title;
        this.description = description;
        this.imageHref = imageHref;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public String getImageHref() {
        return imageHref;
    }


    public String getmTilte() {
        return title;
    }


    public String getmDecription() {
        return description;
    }

}
