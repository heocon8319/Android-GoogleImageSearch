package me.mikeliu.googleimagesearch.controllers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.controllers.adapters.ImageResultsGridAdapter;
import me.mikeliu.googleimagesearch.models.ImageResultsModel;
import me.mikeliu.googleimagesearch.services.GoogleImageFetchTask;
import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchResult;
import me.mikeliu.googleimagesearch.services.messages.SearchCompletedEvent;
import me.mikeliu.googleimagesearch.services.messages.SearchPaginateEvent;
import me.mikeliu.googleimagesearch.services.messages.SearchNewQueryEvent;
import me.mikeliu.googleimagesearch.utils.ActivityUtils;
import me.mikeliu.googleimagesearch.utils.IoC;
import me.mikeliu.googleimagesearch.utils.Utils;
import me.mikeliu.googleimagesearch.views.ImageResultsView;

/**
 * Image results controller
 */
public class ImageResultsFragment
        extends Fragment {
    private static final String TAG = ImageResultsFragment.class.getName();

    private ImageResultsModel _resultsModel = IoC.resolve(ImageResultsModel.class);
    private ImageResultsView _view;
    private ImageResultsGridAdapter _adapter;
    private GoogleImageFetchTask _task;

    private Bus _bus = IoC.resolve(Bus.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView");

        // create data adapter
        _adapter = new ImageResultsGridAdapter(getActivity(), new ArrayList<GoogleImageSearchResult>());
        if (_resultsModel.response != null) {
            _adapter.addAll(_resultsModel.response.Data.Results);
        }

        // create view
        _view = new ImageResultsView(_adapter);
        View resultView = _view.onCreateView(inflater, container, savedInstanceState);

        // listen for events
        _bus.register(this);

        return resultView;
    }

    @Override public void onDestroy() {
        Log.w(TAG, "onDestroy");

        super.onDestroy();

        _bus.unregister(this);

        if (_task != null) {
            _task.cancel(true);
            _resultsModel.isLoading = false;
        }

        _view.dispose();
    }

    private void fetchMoreResults() {
        if (_task != null) {
            _task.cancel(true);
        }

        _resultsModel.isLoading = true;
        _task = new GoogleImageFetchTask();
        _task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _resultsModel);
    }

    @Subscribe public void eventSearchStarted(SearchNewQueryEvent event) {
        _adapter.clear();
        _adapter.notifyDataSetInvalidated();

        _resultsModel.setNewQuery(event.query);

        fetchMoreResults();
    }

    @Subscribe public void eventSearchPaginate(SearchPaginateEvent event) {
        fetchMoreResults();
    }

    @Subscribe public void eventSearchCompleted(SearchCompletedEvent event) {
        switch (event.status) {
            case SearchCompletedEvent.DONE:
            case SearchCompletedEvent.DONE_LASTPAGE:

                // Check search result query is the same as current query
                if (!_resultsModel.query.equalsIgnoreCase(event.query)) return;

                // Merge results if needed
                if (_resultsModel.response == null) {
                    _resultsModel.response = event.response;
                } else {
                    _resultsModel.response.Data.Results = Utils.concat(
                            _resultsModel.response.Data.Results,
                            event.response.Data.Results);
                    _resultsModel.response.Data.Cursor = event.response.Data.Cursor;
                }

                _resultsModel.hasMorePages = event.status == SearchCompletedEvent.DONE;

                _adapter.addAll(event.response.Data.Results);
                _adapter.notifyDataSetChanged();

                break;
            case SearchCompletedEvent.FAILED:
                if (event.statusMessage != null && !event.statusMessage.isEmpty()) {
                    ActivityUtils.toast(event.statusMessage);
                } else {
                    ActivityUtils.toast(getActivity().getString(R.string.image_fetch_failed));
                }

                _resultsModel.hasMorePages = false;

                break;
        }
    }
}
