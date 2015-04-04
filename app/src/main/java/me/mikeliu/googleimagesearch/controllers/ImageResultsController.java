package me.mikeliu.googleimagesearch.controllers;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import me.mikeliu.googleimagesearch.services.MessageBus;
import me.mikeliu.googleimagesearch.views.ImageResultsView;

public class ImageResultsController
        extends ActionBarActivity
        implements MessageBus.Listener {

    private ImageResultsView _view;
    private MessageBus _bus;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        _bus = MessageBus.instance();
        _bus.addListener(MessageBus.E_FETCH_MORE, this);

        _view = new ImageResultsView(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return _view.onCreateOptionsMenu(menu);
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        _view.dispose();
        _bus.removeListener(MessageBus.E_FETCH_MORE, this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        switch(intent.getAction()) {
            case Intent.ACTION_SEARCH: {
                String query = intent.getStringExtra(SearchManager.QUERY);
                newQuery(query);
                break;
            }
        }
    }

    @Override public void notify(String event, Object object) {
        switch(event) {
            case MessageBus.E_FETCH_MORE:
                break;
        }
    }

    private void newQuery(String query) {
        _bus.sendEvent(MessageBus.E_NEW_SEARCH, query);
    }
}
