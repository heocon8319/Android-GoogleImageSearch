package me.mikeliu.googleimagesearch.services.messages;

import me.mikeliu.googleimagesearch.models.ImageResultsModel;

public class NewSearchStartedEvent {
    public final ImageResultsModel model;

    public NewSearchStartedEvent(ImageResultsModel model) {
        this.model = model;
    }
}
