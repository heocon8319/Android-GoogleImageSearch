package me.mikeliu.googleimagesearch.services;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.otto.Bus;

import java.io.IOException;

import me.mikeliu.googleimagesearch.models.ImageResultsModel;
import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchCursor;
import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchResponse;
import me.mikeliu.googleimagesearch.services.messages.SearchCompletedEvent;
import me.mikeliu.googleimagesearch.utils.IoC;
import me.mikeliu.googleimagesearch.utils.Utils;

public class GoogleImageFetchTask extends AsyncTask<ImageResultsModel, Void, SearchCompletedEvent> {
    private static final String TAG = GoogleImageFetchTask.class.getName();

    private static final String REQUEST_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&start=%s&ip=%s";

    private final Bus _bus = IoC.resolve(Bus.class);
    private final OkHttpClient _webClient = IoC.resolve(OkHttpClient.class);

    @Override protected SearchCompletedEvent doInBackground(ImageResultsModel... params) {
        if (params == null || params.length == 0)
            throw new IllegalArgumentException("params must contain a query");

        SearchCompletedEvent result = new SearchCompletedEvent();

        ImageResultsModel model = params[0];

        result.query = model.query;
        String encodedQuery = Uri.encode(result.query);

        // get the next page - TODO: error checking
        int start = 0;
        if (model.response != null) {
            GoogleImageSearchCursor c = model.response.Data.Cursor;
            start = c.Pages[c.PageIndex + 1].Start;
        }

        String requestUrl = String.format(REQUEST_URL, encodedQuery, start, Utils.getUserIp());

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
        } catch (JsonSyntaxException | IllegalStateException e) {
            e.printStackTrace();
            result.status = SearchCompletedEvent.FAILED;
            return result;
        }

        if (r == null
                || r.Data == null
                || r.Data.Cursor == null
                || r.Data.Cursor.Pages == null
                || r.Data.Results == null) {
            result.status = SearchCompletedEvent.FAILED;

            if (r != null && r.Details != null) {
                result.statusMessage = r.Details;
            }

            return result;
        }

        if (r.Data.Cursor.PageIndex >= r.Data.Cursor.Pages.length - 1) {
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
