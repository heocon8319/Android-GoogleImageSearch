package me.mikeliu.googleimagesearch.services.json;

import com.google.gson.annotations.SerializedName;

public class GoogleImageSearchCursor {
    @SerializedName("pages")
    public GoogleImageSearchCursorPage[] Pages;

    @SerializedName("currentPageIndex")
    public int PageIndex;
}
