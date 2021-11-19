package com.example.bestquotes.ImageSharingforUpdateAndroidVersions;

import android.app.Application;
import android.content.Context;

public class SetupContext extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
