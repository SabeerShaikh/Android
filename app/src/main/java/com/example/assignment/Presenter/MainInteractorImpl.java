package com.example.assignment.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

import androidx.room.Room;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment.Interfaces.GetDataListener;
import com.example.assignment.Interfaces.MainInteractor;
import com.example.assignment.DB.AppDatabase;
import com.example.assignment.Util.API;
import com.example.assignment.dataModel.AssignmentDataManager;
import com.example.assignment.dataModel.AssignmentModel;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainInteractorImpl implements MainInteractor {

    private final String REQUEST_TAG = "Demo-Network-Call";
    private GetDataListener mGetDatalistener;
    private final Response.ErrorListener onEQError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mGetDatalistener.onFailure(error.toString());
        }
    };
    private RequestQueue mRequestQueue;
    private AppDatabase mDatabase;
    private SharedPreferences sharedpreferences;
    private final Response.Listener<String> onEQLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<AssignmentModel> assignmentModelsList = new ArrayList<>();
            JSONObject jsonObject;
            JSONArray jsonArray = null;
            String mainTitle = null;
            try {
                jsonObject = new JSONObject(response);
                mainTitle = jsonObject.getString("title");
                jsonArray = jsonObject.getJSONArray("rows");
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("ActivityTitle", mainTitle);
                editor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject1;

                assert jsonArray != null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject1 = jsonArray.getJSONObject(i);
                    AssignmentModel assignmentModel = new AssignmentModel(jsonObject1.getString("title"),
                            jsonObject1.getString("description"),
                            jsonObject1.getString("imageHref"));
                    addToDB(assignmentModel);
                    assignmentModelsList.add(assignmentModel);

                }
                mGetDatalistener.onSuccess(mainTitle, assignmentModelsList);

            } catch (JsonSyntaxException ex) {

                mGetDatalistener.onFailure(ex.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    MainInteractorImpl(GetDataListener mGetDatalistener) {

        this.mGetDatalistener = mGetDatalistener;
    }

    @Override
    public void provideData(Context context, boolean isRestoring) {

        boolean shouldLoadFromNetwork;
        if (isRestoring) {

            List<AssignmentModel> existingData = AssignmentDataManager.getInstance().getLatestData();

            if (existingData != null && !existingData.isEmpty()) {
                // we have cached copy of data for restoring purpose
                shouldLoadFromNetwork = false;
                mGetDatalistener.onSuccess(sharedpreferences.getString("ActivityTitle", ""), existingData);
            } else {
                shouldLoadFromNetwork = true;
            }
        } else {
            shouldLoadFromNetwork = true;
        }

        if (shouldLoadFromNetwork) {

            if (isInternetOn(context)) {
                this.initNetworkCall(context);
            } else {

                mGetDatalistener.onFailure("No internet connection.");
            }
        }
    }

    private void initNetworkCall(Context context) {

        cancelAllRequests();
        sharedpreferences = context.getSharedPreferences(API.MyPREFERENCES, Context.MODE_PRIVATE);
        mDatabase = Room.databaseBuilder(context, AppDatabase.class, "production")
                .build();
        mRequestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, API.ASS_URL, onEQLoaded, onEQError);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, /* 10 sec timeout policy */
                0, /*no retry*/
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setTag(REQUEST_TAG);
        mRequestQueue.add(request);

    }

    private void cancelAllRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void onDestroy() {
        cancelAllRequests();
    }

    private void addToDB(final AssignmentModel assignmentModel) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.databaseInterface().insertAll(assignmentModel);
            }
        });
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
                final Network network = cm.getActiveNetwork();

                if (network != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(network);

                    assert nc != null;
                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }

}
