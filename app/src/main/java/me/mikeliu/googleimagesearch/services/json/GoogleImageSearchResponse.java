package me.mikeliu.googleimagesearch.services.json;

import com.google.gson.annotations.SerializedName;

public class GoogleImageSearchResponse {

    @SerializedName("responseData")
    public GoogleImageSearchResponseData Data;

    @SerializedName("responseDetails")
    public String Details;
}
