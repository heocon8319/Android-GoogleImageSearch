package me.mikeliu.googleimagesearch.views;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.services.messages.NewSearchStartedEvent;
import me.mikeliu.googleimagesearch.services.messages.SearchCompletedEvent;
import me.mikeliu.googleimagesearch.utils.ActivityUtils;
import me.mikeliu.googleimagesearch.utils.IoC;

public class ImageResultsView {
    @InjectView(R.id.gridView) GridView _gridView;
    @InjectView(R.id.progressBar) ProgressBar _progressBar;
    private MenuItem _searchMenuItem;
    private ImageResultsGridAdapter _adapter;

    private ActionBarActivity _activity;
    private Bus _bus = IoC.resolve(Bus.class);

    public ImageResultsView(Context context) {
        _activity = (ActionBarActivity) context;

        View view = View.inflate(_activity, R.layout.activity_main, null);
        _activity.setContentView(view);

        //ActionBar ab = _activity.getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);
        //ab.setHomeButtonEnabled(true);

        ButterKnife.inject(this, view);

        _bus.register(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        _activity.getMenuInflater().inflate(R.menu.menu_main, menu);
        _searchMenuItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) _activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) _searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(_activity.getComponentName()));

        return true;
    }

    public void dispose() {
        _bus.unregister(this);
    }

    @Subscribe public void eventNewSearchStarted(NewSearchStartedEvent event) {
        if (_searchMenuItem != null) _searchMenuItem.collapseActionView();

        _progressBar.setVisibility(View.VISIBLE);

        _activity.setTitle(event.model.query);

        if (_adapter != null) {
            _adapter.clear();
            _adapter.notifyDataSetInvalidated();
        }
    }

    @Subscribe public void eventSearchCompleted(SearchCompletedEvent event) {
        if (event.status == SearchCompletedEvent.FAILED) {
            ActivityUtils.toast("Oops, failed to fetch images.  Please try a new query.");
        }

        _progressBar.setVisibility(View.INVISIBLE);
    }

    @Subscribe public void eventDisplaySearchResultsEvent(SearchCompletedEvent event) {
        if (_adapter == null) {
            _adapter = new ImageResultsGridAdapter(_activity, new ArrayList<>(Arrays.asList(event.response.Data.Results)));
            _gridView.setAdapter(_adapter);
        } else {
            _adapter.addAll(event.response.Data.Results);
            _adapter.notifyDataSetChanged();
        }
    }
}
