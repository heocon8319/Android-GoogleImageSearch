package me.mikeliu.googleimagesearch.models;

import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchResponse;

public class ImageResultsModel {

    /** Current query */
    public String query;

    /** Whether there are more pages */
    public boolean hasMorePages;

    /** Google results response */
    public GoogleImageSearchResponse response;

    /** Are we currently loading more results? */
    public boolean isLoading;

    public void setNewQuery(String query) {
        this.query = query;
        this.hasMorePages = true;
        this.response = null;
        this.isLoading = false;
    }
}
