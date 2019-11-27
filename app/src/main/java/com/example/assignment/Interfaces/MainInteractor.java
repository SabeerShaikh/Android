package com.example.assignment.Interfaces;

import android.content.Context;

public interface MainInteractor {
    void provideData(Context context, boolean isRestoring);

    void onDestroy();

}
