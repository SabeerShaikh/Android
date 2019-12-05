package com.example.assignment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
 * Created by Sabeer Shaikh on 12/04/2019.
 */
public class AssignmentFragment extends Fragment implements MainView, SwipeRefreshLayout.OnRefreshListener {
    private final String LOADING_TAG = "MainActivity_LOADING";
    private final String CONTENT_TAG = "MainActivity_CONTENT";
    private final String STATE_TAG = "MainActivity_KeyForLayoutManagerState";

    private LinearLayoutManager linearLayoutManager;
    private AssignmentDemoAdapter mAssignmenAdapter;
    private List<AssignmentModel> assignmentModelsList;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MainPresenterImpl mMainPresenter;
    private boolean wasLoadingState = false;
    private boolean wasRestoringState = false;
    private Parcelable savedRecyclerLayoutState;
    private SharedPreferences sharedpreferences;
    private AppDatabase mDatabase;
    private AssignmentActivity listener;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (AssignmentActivity) context;
        }
    }
    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getting all savedInstanceState data
        if (savedInstanceState != null) {
            wasLoadingState = savedInstanceState.getBoolean(LOADING_TAG, false);
            wasRestoringState = savedInstanceState.getBoolean(CONTENT_TAG, false);
            savedRecyclerLayoutState = savedInstanceState.getParcelable(STATE_TAG);

        }

    }



    private void OfflineLoadData() {
        // Creates the databases and initializes it.
        mDatabase = Room.databaseBuilder(listener.getApplicationContext(), AppDatabase.class, "production")
                .build();
        //create thread for fetching recodes from database
        //Fetching all recodes from database.
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //Fetching all recodes from database.
                assignmentModelsList = mDatabase.databaseInterface().getAllItems();
                mAssignmenAdapter = new AssignmentDemoAdapter(assignmentModelsList);
                mRecyclerView.setAdapter(mAssignmenAdapter);
                setRecyclerViewCache();
                if (savedRecyclerLayoutState != null) {
                    Objects.requireNonNull(mRecyclerView.getLayoutManager()).onRestoreInstanceState(savedRecyclerLayoutState);

                }
                savedRecyclerLayoutState = null;
            }
        };

        Thread newThread = new Thread(runnable);
        newThread.start();
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.assignment_fragment_layout, parent, false);
    }
    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        //if network is not available
        if (!isInternetOn(listener)) {
            OfflineLoadData();
            listener.setTitle(sharedpreferences.getString("ActivityTitle", ""));
        }

    }
    private void init(View view) {

        //Initialisation of shared preference
        sharedpreferences = listener.getSharedPreferences(API.MyPREFERENCES, Context.MODE_PRIVATE);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
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
    public void onStart() {
        super.onStart();

        mMainPresenter = new MainPresenterImpl(this);

        if (wasLoadingState) {
            // it was loading already so restart fetching anyway
            mMainPresenter.getDataForList(listener.getApplicationContext(), false);
        } else {
            // it was not loading now it wither restores cached data or fetch from network
            mMainPresenter.getDataForList(listener.getApplicationContext(), wasRestoringState);
        }

    }

    @Override
    public void onRefresh() {
        //force refresh
        if (isInternetOn(getActivity()))
            mMainPresenter.getDataForList(listener.getApplicationContext(), false);
        else
            hideProgress();

    }

    //set the recycler view cache for fast loading images
    private void setRecyclerViewCache() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    public void onGetDataSuccess(List<AssignmentModel> list) {
        mAssignmenAdapter = new AssignmentDemoAdapter(list);
        mRecyclerView.setAdapter(mAssignmenAdapter);
        setRecyclerViewCache();

        if (savedRecyclerLayoutState != null) {
            Objects.requireNonNull(mRecyclerView.getLayoutManager()).onRestoreInstanceState(savedRecyclerLayoutState);
        }

        savedRecyclerLayoutState = null;
    }

    @Override
    public void onGetDataFailure(String message) {
        Toast.makeText(listener.getApplicationContext(), message, Toast.LENGTH_LONG).show();

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
        listener.setTitle(sharedpreferences.getString("ActivityTitle", ""));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

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
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            boolean isRestoringVal = false;
            boolean isLoadingState = false;

            if (savedInstanceState != null) {
                isRestoringVal = savedInstanceState.getBoolean(CONTENT_TAG, false);
                isLoadingState = savedInstanceState.getBoolean(LOADING_TAG, false);
            }
            if (isLoadingState) {
                // it was loading already so restart fetching anyway
                mMainPresenter.getDataForList(listener.getApplicationContext(), false);
            } else {
                // it was not loading then, now it whether restores cached data or fetch from network
                mMainPresenter.getDataForList(listener.getApplicationContext(), isRestoringVal);
            }
            assert savedInstanceState != null;
            savedRecyclerLayoutState = savedInstanceState.getParcelable(STATE_TAG);
        }
    }

    @Override
    public void onStop() {
        mMainPresenter.onDestroy();
        super.onStop();
    }
    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
    private boolean isInternetOn(Context context) {

        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

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
