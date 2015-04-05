package me.mikeliu.googleimagesearch.services.messages;

public class NewSearchEvent {
    public final String query;

    public NewSearchEvent(String query) {
        this.query = query;
    }
}
