package me.mikeliu.googleimagesearch.services.json;

import com.google.gson.annotations.SerializedName;

public class GoogleImageSearchResult {

    @SerializedName("tbUrl")
    public String url;

    @SerializedName("titleNoFormatting")
    public String title;
}
