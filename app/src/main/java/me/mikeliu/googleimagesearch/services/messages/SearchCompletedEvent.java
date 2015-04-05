package me.mikeliu.googleimagesearch.services.messages;

import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchResponse;

public class SearchCompletedEvent {
    public final static int FAILED = 0;
    public final static int DONE = 1;
    public final static int DONE_LASTPAGE = 2;

    public String query;
    public int status;
    public GoogleImageSearchResponse response;
}
