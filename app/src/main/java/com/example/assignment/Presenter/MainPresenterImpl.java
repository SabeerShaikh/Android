package com.example.assignment.Presenter;

import android.content.Context;

import com.example.assignment.Interfaces.GetDataListener;
import com.example.assignment.Interfaces.MainInteractor;
import com.example.assignment.Interfaces.MainPresenter;
import com.example.assignment.Interfaces.MainView;
import com.example.assignment.dataModel.AssignmentDataManager;
import com.example.assignment.dataModel.AssignmentModel;

import java.util.List;

public class MainPresenterImpl implements MainPresenter, GetDataListener {

    private MainView mMainView;
    private MainInteractor mInteractor;

    public MainPresenterImpl(MainView mMainView) {
        this.mMainView = mMainView;
        this.mInteractor = new MainInteractorImpl(this);
    }

    @Override
    public void getDataForList(Context context, boolean isRestoring) {

        // get this done by the interactor
        mMainView.showProgress();
        mInteractor.provideData(context, isRestoring);

    }

    @Override
    public void onDestroy() {

        mInteractor.onDestroy();
        if (mMainView != null) {
            mMainView.hideProgress();
            mMainView = null;
        }
    }

    @Override
    public void onSuccess(String title, List<AssignmentModel> list) {

        // updating cache copy of data for restoring purpose
        AssignmentDataManager.getInstance().setLatestData(list);


        if (mMainView != null) {
            mMainView.setMainTitle();
            mMainView.hideProgress();
            mMainView.onGetDataSuccess(list);
        }
    }

    @Override
    public void onFailure(String message) {
        if (mMainView != null) {
            mMainView.hideProgress();
            mMainView.onGetDataFailure(message);
        }

    }

}
