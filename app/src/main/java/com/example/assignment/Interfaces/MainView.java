package com.example.assignment.Interfaces;

import android.os.Bundle;

import com.example.assignment.dataModel.AssignmentModel;

import java.util.List;

/**
 * Created by Sabeer Shaikh on 11/28/19.
 */
public interface MainView {
    void onGetDataSuccess(List<AssignmentModel> list);

    void onGetDataFailure(String message);

    void showProgress();

    void hideProgress();

    void setMainTitle();

    void onRestoreInstanceState(Bundle savedInstanceState);
}
