package com.example.assignment.API;

import android.content.Context;

public interface MainInteractor {
    void provideData(Context context, boolean isRestoring);

    void onDestroy();

}
