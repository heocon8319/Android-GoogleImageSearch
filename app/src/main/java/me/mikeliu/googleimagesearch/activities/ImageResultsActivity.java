package me.mikeliu.googleimagesearch.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import me.mikeliu.googleimagesearch.views.ImageResultsView;

public class ImageResultsActivity extends ActionBarActivity {

    private ImageResultsView _view;
    private ImageResultsView.ViewListener _viewListener = new ImageResultsView.ViewListener() {

    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        _view = new ImageResultsView(this, _viewListener);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return _view.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        switch(intent.getAction()) {
            case Intent.ACTION_SEARCH: {
                String query = intent.getStringExtra(SearchManager.QUERY);
                Log.e("BLAH", "query: " + query);
                break;
            }
        }
    }
}
