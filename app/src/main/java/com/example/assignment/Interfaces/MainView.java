package com.example.assignment.Interfaces;

import com.example.assignment.dataModel.AssignmentModel;

import java.util.List;

public interface MainView {
    void onGetDataSuccess(List<AssignmentModel> list);

    void onGetDataFailure(String message);

    void showProgress();

    void hideProgress();

    void setMainTitle();
}
