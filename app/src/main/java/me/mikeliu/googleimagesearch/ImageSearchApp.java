package me.mikeliu.googleimagesearch;

import android.app.Application;
import android.content.Context;

import com.squareup.picasso.Picasso;

import me.mikeliu.googleimagesearch.utils.IoC;

public class ImageSearchApp extends Application {
    private static ImageSearchApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Our IoC container doesn't support objects without empty constructors yet
        IoC.register(Picasso.with(getContext()));
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
