package me.mikeliu.googleimagesearch.views;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import me.mikeliu.googleimagesearch.ImageSearchApp;
import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.models.ImageResultsModel;
import me.mikeliu.googleimagesearch.services.messages.SearchNewQueryEvent;
import me.mikeliu.googleimagesearch.utils.IoC;

/**
 * View for the main app container
 * We'll handle common View objects here, such as the DrawerLayout and ActionBar
 */
public class AppView {
    @InjectView(R.id.drawer_layout) DrawerLayout _drawer;
    @InjectView(R.id.drawer_fragment) View _drawerSidebar;
    private ActionBarDrawerToggle _drawerToggle;
    private MenuItem _searchMenuItem;
    private ActionBar _actionBar;

    private ImageResultsModel _resultsModel = IoC.resolve(ImageResultsModel.class);
    private Bus _bus = IoC.resolve(Bus.class);

    public AppView(final Context context) {
        ActionBarActivity activity = (ActionBarActivity) context;
        _actionBar = activity.getSupportActionBar();

        View view = View.inflate(context, R.layout.activity_main, null);
        activity.setContentView(view);
        ButterKnife.inject(this, view);

        _bus.register(this);

        _drawerToggle = new ActionBarDrawerToggle(
                activity,
                _drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                updateTitle();
                ((ActionBarActivity) context).invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateTitle();
                ((ActionBarActivity) context).invalidateOptionsMenu();
            }
        };

        _actionBar.setDisplayHomeAsUpEnabled(true);
        _actionBar.setHomeButtonEnabled(true);

        _drawer.post(new Runnable() {
            @Override
            public void run() {
                _drawerToggle.syncState();
            }
        });

        _drawer.setDrawerListener(_drawerToggle);

        updateTitle();
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

    @Subscribe public void eventSearchStarted(SearchNewQueryEvent event) {
        _drawer.closeDrawers();
        if (_searchMenuItem != null) _searchMenuItem.collapseActionView();
        updateTitle();
    }

    private void updateTitle() {
        boolean drawerOpen = isDrawerOpen();

        if (drawerOpen) {
            _actionBar.setTitle(ImageSearchApp.getContext().getString(R.string.history_fragment_title));
        } else {
            if (_resultsModel.query != null && !_resultsModel.query.isEmpty()) {
                _actionBar.setTitle(_resultsModel.query);
            } else {
                _actionBar.setTitle(R.string.app_name);
            }
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = isDrawerOpen();
        _searchMenuItem.setVisible(!drawerOpen);
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
