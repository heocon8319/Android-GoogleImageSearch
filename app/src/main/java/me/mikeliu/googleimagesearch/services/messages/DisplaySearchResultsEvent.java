package me.mikeliu.googleimagesearch.services.messages;

import me.mikeliu.googleimagesearch.models.ImageResultsModel;

public class DisplaySearchResultsEvent {
    public final ImageResultsModel model;

    public DisplaySearchResultsEvent(ImageResultsModel model) {
        this.model = model;
    }
}
