package me.mikeliu.googleimagesearch.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Array;

import me.mikeliu.googleimagesearch.ImageSearchApp;

public class Utils {
    private static final String DefaultIp = "127.0.0.1";

    public static String getUserIp() {
        String result = DefaultIp;

        Context context = ImageSearchApp.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            WifiInfo wifiInfo = wm.getConnectionInfo();
            if (wifiInfo != null) {
                int ipAddress = wifiInfo.getIpAddress();
                result = String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff));
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[] first, T[] second) {
        int firstLength = first.length;
        int secondLength = second.length;

        T[] result = (T[]) Array.newInstance(first.getClass().getComponentType(), firstLength + secondLength);

        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);

        return result;
    }
}
