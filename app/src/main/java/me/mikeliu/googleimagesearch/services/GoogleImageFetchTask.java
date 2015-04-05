package me.mikeliu.googleimagesearch.services;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.otto.Bus;

import java.io.IOException;

import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchResponse;
import me.mikeliu.googleimagesearch.services.messages.SearchCompletedEvent;
import me.mikeliu.googleimagesearch.utils.IoC;

public class GoogleImageFetchTask extends AsyncTask<String, Void, SearchCompletedEvent> {
    private static final String TAG = GoogleImageFetchTask.class.getName();

    private static final String REQUEST_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s";

    private final Bus _bus = IoC.resolve(Bus.class);
    private final OkHttpClient _webClient = IoC.resolve(OkHttpClient.class);

    @Override protected SearchCompletedEvent doInBackground(String... params) {
        if (params == null || params.length == 0)
            throw new IllegalArgumentException("params must contain a query");

        SearchCompletedEvent result = new SearchCompletedEvent();

        result.query = params[0];
        String encodedQuery = Uri.encode(result.query);
        String requestUrl = String.format(REQUEST_URL, encodedQuery);

        Log.v(TAG, requestUrl);

        Request request = new Request.Builder().url(requestUrl).build();
        String responseStr;
        try {
            Response response = _webClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            responseStr = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            result.status = SearchCompletedEvent.FAILED;
            return result;
        }

        if (responseStr == null) {
            result.status = SearchCompletedEvent.FAILED;
            return result;
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        GoogleImageSearchResponse r;

        try {
            r = gson.fromJson(responseStr, GoogleImageSearchResponse.class);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            result.status = SearchCompletedEvent.FAILED;
            return result;
        }

        if (r.Data.Cursor.PageIndex >= r.Data.Cursor.Pages.length) {
            result.status = SearchCompletedEvent.DONE_LASTPAGE;
        } else {
            result.status = SearchCompletedEvent.DONE;
        }

        result.response = r;
        return result;
    }

    @Override
    protected void onPostExecute(SearchCompletedEvent event) {
        _bus.post(event);
    }
}
