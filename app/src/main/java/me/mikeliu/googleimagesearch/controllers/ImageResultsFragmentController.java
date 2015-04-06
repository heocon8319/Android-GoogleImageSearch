package me.mikeliu.googleimagesearch.controllers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import me.mikeliu.googleimagesearch.controllers.adapters.ImageResultsGridAdapter;
import me.mikeliu.googleimagesearch.models.ImageResultsModel;
import me.mikeliu.googleimagesearch.services.GoogleImageFetchTask;
import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchResult;
import me.mikeliu.googleimagesearch.services.messages.SearchCompletedEvent;
import me.mikeliu.googleimagesearch.services.messages.SearchStartedEvent;
import me.mikeliu.googleimagesearch.utils.ActivityUtils;
import me.mikeliu.googleimagesearch.utils.IoC;
import me.mikeliu.googleimagesearch.views.ImageResultsView;

/**
 * Fragment controller for the view containing image results.
 */
public class ImageResultsFragmentController extends Fragment {

    private Bus _bus = IoC.resolve(Bus.class);
    private ImageResultsModel _resultsModel = IoC.resolve(ImageResultsModel.class);
    private GoogleImageFetchTask _task;
    private ImageResultsGridAdapter _adapter;
    private ImageResultsView _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // create data adapter
        _adapter = new ImageResultsGridAdapter(getActivity(), new ArrayList<GoogleImageSearchResult>());

        // create view
        _view = new ImageResultsView(_adapter);
        View resultView = _view.onCreateView(inflater, container, savedInstanceState);

        // listen for events
        _bus.register(this);

        return resultView;
    }

    @Override public void onDestroy() {
        super.onDestroy();

        _view.dispose();
        _bus.unregister(this);
    }

    @Subscribe public void eventSearchStarted(SearchStartedEvent event) {
        _resultsModel.query = event.query;
        _resultsModel.hasMorePages = true;
        _resultsModel.response = null;

        _adapter.clear();
        _adapter.notifyDataSetInvalidated();

        if (_task != null) {
            _task.cancel(true);
        }

        _task = new GoogleImageFetchTask();
        _task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, event.query);
    }

    @Subscribe public void eventSearchCompleted(SearchCompletedEvent event) {
        switch (event.status) {
            case SearchCompletedEvent.DONE:
            case SearchCompletedEvent.DONE_LASTPAGE:

                // Check search result query is the same as current query
                if (!_resultsModel.query.equalsIgnoreCase(event.query)) return;

                // TODO: merge pagination calls
                _resultsModel.response = event.response;
                _resultsModel.hasMorePages = event.status == SearchCompletedEvent.DONE;

                _adapter.addAll(event.response.Data.Results);
                _adapter.notifyDataSetChanged();

                break;
            case SearchCompletedEvent.FAILED:
                ActivityUtils.toast("Failed to fetch images.  Please try a new query.");
                break;
        }
    }
}
