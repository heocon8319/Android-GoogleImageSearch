package me.mikeliu.googleimagesearch.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.controllers.adapters.ImageResultsGridAdapter;
import me.mikeliu.googleimagesearch.services.messages.SearchStartedEvent;
import me.mikeliu.googleimagesearch.services.messages.SearchCompletedEvent;
import me.mikeliu.googleimagesearch.utils.IoC;

public class ImageResultsView {
    @InjectView(R.id.gridView) GridView _gridView;
    @InjectView(R.id.progressBar) ProgressBar _progressView;
    private ImageResultsGridAdapter _adapter;

    private Bus _bus = IoC.resolve(Bus.class);

    public ImageResultsView(ImageResultsGridAdapter adapter) {
        _adapter = adapter;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_results, container, false);
        ButterKnife.inject(this, view);

        _gridView.setAdapter(_adapter);

        _bus.register(this);

        return view;
    }

    public void dispose() {
        _bus.unregister(this);
    }

    @Subscribe public void eventSearchStarted(SearchStartedEvent event) {
        _progressView.setVisibility(View.VISIBLE);
    }

    @Subscribe public void eventSearchCompleted(SearchCompletedEvent event) {
        _progressView.setVisibility(View.INVISIBLE);
    }
}
