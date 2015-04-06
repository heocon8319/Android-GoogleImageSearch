package me.mikeliu.googleimagesearch.services.messages;

public class SearchNewQueryEvent {
    public final String query;

    public SearchNewQueryEvent(String query) {
        this.query = query;
    }
}
