package me.mikeliu.googleimagesearch.views;

public abstract class BaseView {
    private ViewListener _viewListener;

    public void setViewListener(ViewListener viewListener) {
        _viewListener = viewListener;
    }

    public static interface ViewListener {
    }
}
