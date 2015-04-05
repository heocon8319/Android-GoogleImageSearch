package me.mikeliu.googleimagesearch.services.json;

import com.google.gson.annotations.SerializedName;

public class GoogleImageSearchResult {
    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

    @SerializedName("tbWidth")
    public int thumbWidth;

    @SerializedName("tbHeight")
    public int thumbHeight;

    @SerializedName("tbUrl")
    public String url;

    @SerializedName("titleNoFormatting")
    public String title;

    @SerializedName("visibleUrl")
    public String visibleUrl;

    @SerializedName("contentNoFormatting")
    public String content;
}
