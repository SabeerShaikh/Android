package com.example.assignment.API;

import com.example.assignment.dataModel.AssignmentModel;

import java.util.List;

public interface GetDataListener {
    void onSuccess(String message, List<AssignmentModel> list);

    void onFailure(String message);
}
