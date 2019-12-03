package com.example.assignment.Interfaces;

import com.example.assignment.dataModel.AssignmentModel;

import java.util.List;

/**
 * Created by Sabeer Shaikh on 11/28/19.
 */
public interface GetDataListener {
    void onSuccess(String message, List<AssignmentModel> list);

    void onFailure(String message);
}
