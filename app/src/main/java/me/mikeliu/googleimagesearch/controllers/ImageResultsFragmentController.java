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

import me.mikeliu.googleimagesearch.R;
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
        super.onDestroy();

        _view.dispose();
        _bus.unregister(this);
    }

    @Subscribe public void eventSearchStarted(SearchStartedEvent event) {
        if (event.model.response == null) {
            _adapter.clear();
            _adapter.notifyDataSetInvalidated();
        }

        if (_task != null) {
            _task.cancel(true);
        }

        _resultsModel.isLoading = true;

        _task = new GoogleImageFetchTask();
        _task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, event.model);
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
                    GoogleImageSearchResult[] oldResults = _resultsModel.response.Data.Results;
                    GoogleImageSearchResult[] newResults = event.response.Data.Results;
                    GoogleImageSearchResult[] combinedResults = new GoogleImageSearchResult[
                            oldResults.length + newResults.length];
                    System.arraycopy(oldResults, 0, combinedResults, 0, oldResults.length);
                    System.arraycopy(newResults, 0, combinedResults, oldResults.length, newResults.length);
                    _resultsModel.response.Data.Results = combinedResults;
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
