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
import me.mikeliu.googleimagesearch.services.MessageBus;

public class ImageResultsView
        implements MessageBus.Listener{
    @InjectView(R.id.gridView) GridView _gridView;
    private MenuItem _searchMenuItem;

    private Activity _ctrl;
    private MessageBus _bus;

    public ImageResultsView(Context context) {
        _ctrl = (Activity) context;

        _bus = MessageBus.instance();
        _bus.addListener(MessageBus.E_NEW_SEARCH, this);

        View view = View.inflate(_ctrl, R.layout.activity_main, null);
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

    public void dispose() {
        _bus.removeListener(MessageBus.E_NEW_SEARCH, this);
    }

    @Override public void notify(String event, Object object) {
        switch(event) {
            case MessageBus.E_NEW_SEARCH:
                _searchMenuItem.collapseActionView();
                break;
        }
    }
}
