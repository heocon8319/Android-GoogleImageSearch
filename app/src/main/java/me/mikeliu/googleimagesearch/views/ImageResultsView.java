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
import me.mikeliu.googleimagesearch.services.messages.SearchPaginateEvent;
import me.mikeliu.googleimagesearch.services.messages.SearchNewQueryEvent;
import me.mikeliu.googleimagesearch.utils.IoC;

public class ImageResultsView implements AbsListView.OnScrollListener {
    /** Min number of results to load before infinite scroll starts */
    private static final int GRID_VIEW_RESULTS_MIN = 20;

    /** For infinite scroll: min number of results we want to pre-load on the next page of results */
    private static final int GRID_VIEW_RESULTS_BUFFER = 12;

    @InjectView(R.id.gridView) GridView _gridView;
    @InjectView(R.id.progressBar) ProgressBar _progressView;

    private ImageResultsGridAdapter _adapter;
    private Bus _bus = IoC.resolve(Bus.class);
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

    @Subscribe public void eventSearchStarted(SearchNewQueryEvent event) {
        _progressView.setVisibility(View.VISIBLE);
    }

    @Subscribe public void eventSearchPaginate(SearchPaginateEvent event) {
        _progressView.setVisibility(View.VISIBLE);
    }

    @Subscribe public void eventSearchCompleted(SearchCompletedEvent event) {
        switch(event.status) {
            case SearchCompletedEvent.DONE_LASTPAGE:
            case SearchCompletedEvent.FAILED:
                _progressView.setVisibility(View.INVISIBLE);
                _resultsModel.isLoading = false;
                break;
            case SearchCompletedEvent.DONE:
                if (_adapter.getCount() <= GRID_VIEW_RESULTS_MIN) {
                    SearchPaginateEvent paginateEvent = new SearchPaginateEvent();
                    _bus.post(paginateEvent);
                } else {
                    _progressView.setVisibility(View.INVISIBLE);
                    _resultsModel.isLoading = false;
                }

                break;
        }
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) { }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (_resultsModel.isLoading || !_resultsModel.hasMorePages) {
            return;
        }

        if (totalItemCount <= firstVisibleItem + visibleItemCount + GRID_VIEW_RESULTS_BUFFER) {
            SearchPaginateEvent paginateEvent = new SearchPaginateEvent();
            _bus.post(paginateEvent);
        }
    }
}
