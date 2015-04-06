package me.mikeliu.googleimagesearch.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mikeliu.googleimagesearch.R;

public class HistoryView {
    @InjectView(R.id.list_view) ListView _listView;
    private ListAdapter _adapter;

    public HistoryView(ListAdapter listAdapter) {
        _adapter = listAdapter;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        if (rootView == null) throw new NullPointerException("rootView");
        ButterKnife.inject(this, rootView);

        _listView.setAdapter(_adapter);

        return rootView;
    }
}
