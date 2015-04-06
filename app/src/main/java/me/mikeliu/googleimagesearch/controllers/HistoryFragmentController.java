package me.mikeliu.googleimagesearch.controllers;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.services.storage.DatabaseHelper;
import me.mikeliu.googleimagesearch.services.storage.HistoryContentProvider;
import me.mikeliu.googleimagesearch.views.HistoryView;

/**
 * Fragment controller for the history view
 */
public class HistoryFragmentController
        extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter _historyAdapter;
    private HistoryContentObserver _contentObserver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // create data adapter
        _historyAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_history,
                null,
                new String[] { DatabaseHelper.COLUMN_QUERY },
                new int[] { R.id.text_view },
                0);

        // create view
        HistoryView fragmentView = new HistoryView(_historyAdapter);
        View view = fragmentView.onCreateView(inflater, container, savedInstanceState);

        // load data
        getLoaderManager().initLoader(0, null, this);

        // observe data changes
        _contentObserver = new HistoryContentObserver(new Handler());
        getActivity().getContentResolver().registerContentObserver(
                HistoryContentProvider.CONTENT_URI,
                true,
                _contentObserver
        );

        return view;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if (_contentObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(_contentObserver);
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                HistoryContentProvider.CONTENT_URI,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_ID + " DESC");
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        _historyAdapter.swapCursor(data);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        _historyAdapter.swapCursor(null);
    }

    class HistoryContentObserver extends ContentObserver {
        public HistoryContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (!isAdded()) return;

            getLoaderManager().restartLoader(0, null, HistoryFragmentController.this);
        }
    }
}
