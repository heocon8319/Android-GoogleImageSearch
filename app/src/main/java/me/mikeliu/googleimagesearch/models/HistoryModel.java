package me.mikeliu.googleimagesearch.models;

import android.database.Cursor;

import me.mikeliu.googleimagesearch.services.storage.DatabaseHelper;

public class HistoryModel {
    public String query;

    public static HistoryModel createFromCursor(Cursor cursor) {
        HistoryModel result = new HistoryModel();
        result.query = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUERY));
        return result;
    }
}
