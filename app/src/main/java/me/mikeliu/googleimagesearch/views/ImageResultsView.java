package me.mikeliu.googleimagesearch.views;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mikeliu.googleimagesearch.R;

public class ImageResultsView extends BaseView {
    @InjectView(R.id.gridView) GridView _gridView;
    private MenuItem _searchMenuItem;

    private Activity _ctrl;

    public ImageResultsView(Context context, ViewListener _viewListener) {
        _ctrl = (Activity) context;

        View view = View.inflate(_ctrl, R.layout.activity_main, null);
        setViewListener(_viewListener);
        _ctrl.setContentView(view);

        ButterKnife.inject(this, view);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        _ctrl.getMenuInflater().inflate(R.menu.menu_main, menu);
        _searchMenuItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) _ctrl.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) _searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(_ctrl.getComponentName()));

        return true;
    }
}
