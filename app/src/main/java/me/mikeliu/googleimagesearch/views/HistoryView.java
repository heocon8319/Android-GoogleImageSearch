package me.mikeliu.googleimagesearch.views;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.squareup.otto.Bus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.services.messages.SearchStartedEvent;
import me.mikeliu.googleimagesearch.services.storage.DatabaseHelper;
import me.mikeliu.googleimagesearch.utils.IoC;

public class HistoryView {
    @InjectView(R.id.list_view) ListView _listView;

    private ListAdapter _adapter;
    private Bus _bus = IoC.resolve(Bus.class);

    public HistoryView(ListAdapter listAdapter) {
        _adapter = listAdapter;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        if (rootView == null) throw new NullPointerException("rootView");
        ButterKnife.inject(this, rootView);

        _listView.setAdapter(_adapter);
        _listView.setOnItemClickListener(new ListViewItemClickListener());

        return rootView;
    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) _adapter.getItem(position);
            String query = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUERY));

            SearchStartedEvent event = new SearchStartedEvent(query);
            _bus.post(event);
        }
    }
}
