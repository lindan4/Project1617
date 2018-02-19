package com.flashitdelivery.flash_it.models.remote.body;

/**
 * Created by yon on 19/08/16.
 */
public class GetSuggestionsBody {
    String query;
    public GetSuggestionsBody(String query) {
        this.query = query;
    }
}
