package me.mikeliu.googleimagesearch;

import android.app.Application;
import android.content.Context;

public class ImageSearchApp extends Application {
    private static ImageSearchApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
