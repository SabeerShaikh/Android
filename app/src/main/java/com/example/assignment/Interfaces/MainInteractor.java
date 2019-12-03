package com.example.assignment.Interfaces;

import android.content.Context;

/**
 * Created by Sabeer Shaikh on 11/28/19.
 */
public interface MainInteractor {
    void provideData(Context context, boolean isRestoring);

    void onDestroy();

}
