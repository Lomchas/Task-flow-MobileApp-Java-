package com.example.taskflow;

import android.app.Application;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class TaskFlowApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa el SDK de Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
