package me.mikeliu.googleimagesearch.services.json;

import com.google.gson.annotations.SerializedName;

public class GoogleImageSearchCursor {
    @SerializedName("pages")
    public GoogleImageSearchCursorPage[] Pages;

    @SerializedName("estimatedResultCount")
    public long EstimatedResults;

    @SerializedName("currentPageIndex")
    public int PageIndex;

    @SerializedName("moreResultsUrl")
    public String MoreResultsUrl;
}
