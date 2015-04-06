package me.mikeliu.googleimagesearch.controllers;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Bus;

import me.mikeliu.googleimagesearch.services.messages.SearchNewQueryEvent;
import me.mikeliu.googleimagesearch.services.storage.HistoryContentProvider;
import me.mikeliu.googleimagesearch.utils.IoC;
import me.mikeliu.googleimagesearch.views.AppView;

/**
 * Main controller for the app
 */
public class BaseActivity
        extends ActionBarActivity {

    private AppView _view;
    private Bus _bus = IoC.resolve(Bus.class);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _view = new AppView(this);

        _bus.register(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return _view.onCreateOptionsMenu(this, menu);
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        _bus.unregister(this);
        _view.dispose();
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH: {
                String query = intent.getStringExtra(SearchManager.QUERY);

                if (query != null && !query.isEmpty()) {
                    SearchNewQueryEvent event = new SearchNewQueryEvent(query);
                    _bus.post(event);

                    HistoryContentProvider.insertQuery(query);
                }

                break;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        _view.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return _view.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
