package com.example.assignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.assignment.Adapter.AssignmentDemoAdapter;
import com.example.assignment.DB.AppDatabase;
import com.example.assignment.Interfaces.MainView;
import com.example.assignment.Presenter.MainPresenterImpl;
import com.example.assignment.Util.API;
import com.example.assignment.dataModel.AssignmentModel;

import java.util.List;
import java.util.Objects;

/**
 * Created by Sabeer Shaikh on 11/28/19.
 */
public class AssignmentActivity extends AppCompatActivity implements MainView, SwipeRefreshLayout.OnRefreshListener {
    private final String LOADING_TAG = "MainActivity_LOADING";
    private final String CONTENT_TAG = "MainActivity_CONTENT";
    private final String STATE_TAG = "MainActivity_KeyForLayoutManagerState";

    LinearLayoutManager linearLayoutManager;
    AssignmentDemoAdapter mAssignmenAdapter;
    List<AssignmentModel> assignmentModelsList;
    Runnable runnable;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MainPresenterImpl mMainPresenter;
    private boolean wasLoadingState = false;
    private boolean wasRestoringState = false;
    private Parcelable savedRecyclerLayoutState;
    private SharedPreferences sharedpreferences;
    private AppDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        //getting all savedInstanceState data
        if (savedInstanceState != null) {
            wasLoadingState = savedInstanceState.getBoolean(LOADING_TAG, false);
            wasRestoringState = savedInstanceState.getBoolean(CONTENT_TAG, false);
            savedRecyclerLayoutState = savedInstanceState.getParcelable(STATE_TAG);

        }
        //if network is not available
        if (!isInternetOn(this)) {
            OfflineLoadData();
            this.setTitle(sharedpreferences.getString("ActivityTitle", ""));
        }

    }

    private void OfflineLoadData() {
        // Creates the databases and initializes it.
        mDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .build();
        //create thread for fetching recodes from database
        runnable = new Runnable() {
            @Override
            public void run() {
                //Fetching all recodes from database.
                assignmentModelsList = mDatabase.databaseInterface().getAllItems();
                mAssignmenAdapter = new AssignmentDemoAdapter(assignmentModelsList);
                mRecyclerView.setAdapter(mAssignmenAdapter);
                if (savedRecyclerLayoutState != null) {
                    Objects.requireNonNull(mRecyclerView.getLayoutManager()).onRestoreInstanceState(savedRecyclerLayoutState);

                }
                savedRecyclerLayoutState = null;
            }
        };

        Thread newThread = new Thread(runnable);
        newThread.start();
    }

    private void init() {

        //Initialisation of shared preference
        sharedpreferences = getSharedPreferences(API.MyPREFERENCES, Context.MODE_PRIVATE);
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // Setup refresh listener which triggers new data loading
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // Configure the refreshing colors
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    @Override
    protected void onStart() {
        super.onStart();

        mMainPresenter = new MainPresenterImpl(this);

        if (wasLoadingState) {
            // it was loading already so restart fetching anyway
            mMainPresenter.getDataForList(getApplicationContext(), false);
        } else {
            // it was not loading now it wither restores cached data or fetch from network
            mMainPresenter.getDataForList(getApplicationContext(), wasRestoringState);
        }

    }

    @Override
    public void onRefresh() {
        //force refresh
        if (isInternetOn(this))
            mMainPresenter.getDataForList(getApplicationContext(), false);
        else
            hideProgress();

    }

    @Override
    public void onGetDataSuccess(List<AssignmentModel> list) {
        mAssignmenAdapter = new AssignmentDemoAdapter(list);
        mRecyclerView.setAdapter(mAssignmenAdapter);
        if (savedRecyclerLayoutState != null) {
            Objects.requireNonNull(mRecyclerView.getLayoutManager()).onRestoreInstanceState(savedRecyclerLayoutState);

        }
        savedRecyclerLayoutState = null;
    }

    @Override
    public void onGetDataFailure(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }

    @Override
    public void showProgress() {
        hideProgress();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void setMainTitle() {
        this.setTitle(sharedpreferences.getString("ActivityTitle", ""));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (mAssignmenAdapter != null && mAssignmenAdapter.getItemCount() != 0) {
            // for data restoring purpose
            outState.putBoolean(CONTENT_TAG, true);
        } else {
            outState.putBoolean(CONTENT_TAG, false);
        }

        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            // saving the loading state
            outState.putBoolean(LOADING_TAG, true);
        } else {
            outState.putBoolean(LOADING_TAG, false);
        }

        outState.putParcelable(STATE_TAG, linearLayoutManager.onSaveInstanceState());


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        boolean isRestoringVal = false;
        boolean isLoadingState = false;

        if (savedInstanceState != null) {
            isRestoringVal = savedInstanceState.getBoolean(CONTENT_TAG, false);
            isLoadingState = savedInstanceState.getBoolean(LOADING_TAG, false);
        }
        if (isLoadingState) {
            // it was loading already so restart fetching anyway
            mMainPresenter.getDataForList(getApplicationContext(), false);
        } else {
            // it was not loading then, now it whether restores cached data or fetch from network
            mMainPresenter.getDataForList(getApplicationContext(), isRestoringVal);
        }
        assert savedInstanceState != null;
        savedRecyclerLayoutState = savedInstanceState.getParcelable(STATE_TAG);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        mMainPresenter.onDestroy();
        super.onStop();
    }

    private boolean isInternetOn(Context context) {

        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }
}
