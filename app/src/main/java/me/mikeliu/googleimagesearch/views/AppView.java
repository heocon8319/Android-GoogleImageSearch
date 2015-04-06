package me.mikeliu.googleimagesearch.views;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.models.ImageResultsModel;
import me.mikeliu.googleimagesearch.services.messages.SearchStartedEvent;
import me.mikeliu.googleimagesearch.utils.IoC;

/**
 * View for the app container.
 * We'll handle UI common to all screens here, such as the action bar.
 */
public class AppView {
    @InjectView(R.id.drawer_layout) DrawerLayout _drawer;
    @InjectView(R.id.drawer_fragment) View _drawerSidebar;

    private ActionBarDrawerToggle _drawerToggle;
    private ImageResultsModel _resultsModel = IoC.resolve(ImageResultsModel.class);

    private MenuItem _searchMenuItem;
    private Bus _bus = IoC.resolve(Bus.class);
    private ActionBarActivity _activity;

    public AppView(Context context) {
        _activity = (ActionBarActivity) context;
        View view = View.inflate(context, R.layout.activity_main, null);
        _activity.setContentView(view);
        ButterKnife.inject(this, view);

        _bus.register(this);

        _drawerToggle = new ActionBarDrawerToggle(
                _activity,
                _drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                updateTitle();
                _activity.invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateTitle();
                _activity.invalidateOptionsMenu();
            }
        };

        _activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _activity.getSupportActionBar().setHomeButtonEnabled(true);

        _drawer.post(new Runnable() {
            @Override
            public void run() {
                _drawerToggle.syncState();
            }
        });

        _drawer.setDrawerListener(_drawerToggle);
    }

    public void dispose() {
        _bus.unregister(this);
    }

    public boolean onCreateOptionsMenu(Activity activity, Menu menu) {
        activity.getMenuInflater().inflate(R.menu.menu_main, menu);
        _searchMenuItem = menu.findItem(R.id.search);

        // Initialize SearchView in action bar
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) _searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));

        return true;
    }

    @Subscribe public void eventSearchStarted(SearchStartedEvent event) {
        if (_searchMenuItem != null) _searchMenuItem.collapseActionView();
        updateTitle();
    }

    private void updateTitle() {
        boolean drawerOpen = isDrawerOpen();

        if (drawerOpen) {
            _activity.getSupportActionBar().setTitle(_activity.getString(R.string.history_fragment_title));
        } else {
            if (_resultsModel.query != null && !_resultsModel.query.isEmpty()) {
                _activity.getSupportActionBar().setTitle(_resultsModel.query);
            } else {
                _activity.getSupportActionBar().setTitle(R.string.app_name);
            }
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = isDrawerOpen();
        menu.findItem(R.id.search).setVisible(!drawerOpen);
        if (drawerOpen) {
            _searchMenuItem.collapseActionView();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return false;
    }

    private boolean isDrawerOpen() {
        return _drawer.isDrawerOpen(_drawerSidebar);
    }
}
