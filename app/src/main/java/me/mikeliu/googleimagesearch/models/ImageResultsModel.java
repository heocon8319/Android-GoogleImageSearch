package me.mikeliu.googleimagesearch.models;

import me.mikeliu.googleimagesearch.services.json.GoogleImageSearchResponse;

public class ImageResultsModel {
    public String query;
    public boolean hasMorePages;
    public GoogleImageSearchResponse response;
}
