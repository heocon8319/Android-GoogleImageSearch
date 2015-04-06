package me.mikeliu.googleimagesearch.utils;

import android.widget.Toast;

import me.mikeliu.googleimagesearch.ImageSearchApp;

public final class ActivityUtils {
    private static Toast _toast;

    public static void toast(String text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    public static void toast(String text, int duration) {
        clearToast();

        Toast toast = Toast.makeText(ImageSearchApp.getContext(), text, duration);
        toast.show();
        _toast = toast;
    }

    public static void clearToast() {
        if (_toast != null) _toast.cancel();
    }
}

