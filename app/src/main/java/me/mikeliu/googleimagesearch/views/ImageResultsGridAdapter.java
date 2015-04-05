package me.mikeliu.googleimagesearch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mikeliu.googleimagesearch.R;
import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchResult;
import me.mikeliu.googleimagesearch.utils.IoC;

public class ImageResultsGridAdapter extends ArrayAdapter<GoogleImageSearchResult> {
    LayoutInflater _inflater;
    Picasso _imageLoader = IoC.resolve(Picasso.class);

    public ImageResultsGridAdapter(Context context, ArrayList<GoogleImageSearchResult> results) {
        super(context, R.layout.item_image, results);
        _inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = _inflater.inflate(R.layout.item_image, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        GoogleImageSearchResult result = getItem(position);

        _imageLoader.load(result.url).into(holder.imageView);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.imageView) ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
