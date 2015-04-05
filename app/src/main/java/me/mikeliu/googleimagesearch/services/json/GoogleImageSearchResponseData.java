package me.mikeliu.googleimagesearch.services.json;

import com.google.gson.annotations.SerializedName;

public class GoogleImageSearchResponseData {
    @SerializedName("results")
    public GoogleImageSearchResult[] Results;

    @SerializedName("cursor")
    public GoogleImageSearchCursor Cursor;
}
