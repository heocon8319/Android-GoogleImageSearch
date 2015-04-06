package me.mikeliu.googleimagesearch.services.messages;

public class SearchStartedEvent {
    public final String query;

    public SearchStartedEvent(String query) {
        this.query = query;
    }
}
