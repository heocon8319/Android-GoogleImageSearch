package me.mikeliu.googleimagesearch.controllers;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import me.mikeliu.googleimagesearch.models.ImageResultsModel;
import me.mikeliu.googleimagesearch.services.GoogleImageFetchTask;
import me.mikeliu.googleimagesearch.services.messages.DisplaySearchResultsEvent;
import me.mikeliu.googleimagesearch.services.messages.NewSearchEvent;
import me.mikeliu.googleimagesearch.services.messages.NewSearchStartedEvent;
import me.mikeliu.googleimagesearch.services.messages.SearchCompletedEvent;
import me.mikeliu.googleimagesearch.utils.IoC;
import me.mikeliu.googleimagesearch.views.ImageResultsView;

public class ImageResultsController
        extends ActionBarActivity {

    private ImageResultsView _view;
    private Bus _bus = IoC.resolve(Bus.class);
    private ImageResultsModel _resultsModel = IoC.resolve(ImageResultsModel.class);
    private GoogleImageFetchTask _task;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        _bus.register(this);

        _view = new ImageResultsView(this);

        _bus.post(new NewSearchEvent("funny cats"));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return _view.onCreateOptionsMenu(menu);
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        _view.dispose();
        _bus.unregister(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH: {
                String query = intent.getStringExtra(SearchManager.QUERY);

                NewSearchEvent event = new NewSearchEvent(query);
                _bus.post(event);

                break;
            }
        }
    }

    @Subscribe public void eventNewSearch(NewSearchEvent event) {
        String query = event.query;
        if (query == null || query.isEmpty()) return;

        _resultsModel.query = query;
        _resultsModel.hasMorePages = true;
        _resultsModel.response = null;

        _bus.post(new NewSearchStartedEvent(_resultsModel));

        if (_task != null) {
            _task.cancel(true);
        }

        _task = new GoogleImageFetchTask();
        _task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
    }

    @Subscribe public void eventSearchCompleted(SearchCompletedEvent event) {
        switch (event.status) {
            case SearchCompletedEvent.DONE:
            case SearchCompletedEvent.DONE_LASTPAGE:

                // Check search result query is the same as current query
                if (!_resultsModel.query.equalsIgnoreCase(event.query)) return;

                // TODO: merge pagination calls
                _resultsModel.response = event.response;
                _resultsModel.hasMorePages = event.status == SearchCompletedEvent.DONE;

                DisplaySearchResultsEvent outEvent = new DisplaySearchResultsEvent(_resultsModel);
                _bus.post(outEvent);

                break;
        }
    }
}
