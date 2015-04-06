package me.mikeliu.googleimagesearch.services.messages;

import me.mikeliu.googleimagesearch.models.ImageResultsModel;

public class SearchStartedEvent {
    public final ImageResultsModel model;

    public SearchStartedEvent(ImageResultsModel model) {
        this.model = model;
    }
}
