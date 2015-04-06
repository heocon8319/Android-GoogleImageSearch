package me.mikeliu.googleimagesearch.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.controllers.adapters.ImageResultsGridAdapter;
import me.mikeliu.googleimagesearch.models.ImageResultsModel;
import me.mikeliu.googleimagesearch.services.messages.SearchCompletedEvent;
import me.mikeliu.googleimagesearch.services.messages.SearchStartedEvent;
import me.mikeliu.googleimagesearch.utils.IoC;

public class ImageResultsView implements AbsListView.OnScrollListener {
    /** Min number of results to load before infinite scroll starts */
    private static final int GRID_VIEW_RESULTS_MIN = 24;

    /** For infinite scroll: number of results we want to pre-load on the next page of results */
    private static final int GRID_VIEW_RESULTS_BUFFER = 12;

    @InjectView(R.id.gridView) GridView _gridView;
    @InjectView(R.id.progressBar) ProgressBar _progressView;

    private ImageResultsGridAdapter _adapter;
    private Bus _bus = IoC.resolve(Bus.class);
    private boolean _paginationEnabled;
    private boolean _loadingMoreResults;
    private ImageResultsModel _resultsModel = IoC.resolve(ImageResultsModel.class);

    public ImageResultsView(ImageResultsGridAdapter adapter) {
        _adapter = adapter;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_results, container, false);
        ButterKnife.inject(this, view);

        _gridView.setAdapter(_adapter);

        _bus.register(this);

        _gridView.setOnScrollListener(this);

        return view;
    }

    public void dispose() {
        _bus.unregister(this);
    }

    @Subscribe public void eventSearchStarted(SearchStartedEvent event) {
        _progressView.setVisibility(View.VISIBLE);
        _paginationEnabled = true;
        _loadingMoreResults = true;
    }

    @Subscribe public void eventSearchCompleted(SearchCompletedEvent event) {
        switch(event.status) {
            case SearchCompletedEvent.DONE_LASTPAGE:
            case SearchCompletedEvent.FAILED:
                _progressView.setVisibility(View.INVISIBLE);
                _paginationEnabled = false;
                _loadingMoreResults = false;
                break;
            case SearchCompletedEvent.DONE:
                if (_adapter.getCount() < GRID_VIEW_RESULTS_MIN) {
                    SearchStartedEvent newSearch = new SearchStartedEvent(_resultsModel);
                    _bus.post(newSearch);
                } else {
                    _progressView.setVisibility(View.INVISIBLE);
                    _loadingMoreResults = false;
                }

                break;
        }
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) { }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (_loadingMoreResults || !_paginationEnabled || !_resultsModel.hasMorePages) {
            return;
        }

        if (totalItemCount <= firstVisibleItem + visibleItemCount + GRID_VIEW_RESULTS_BUFFER) {
            SearchStartedEvent newSearch = new SearchStartedEvent(_resultsModel);
            _bus.post(newSearch);
        }
    }
}
