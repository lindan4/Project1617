package com.flashitdelivery.flash_it.event;

import com.flashitdelivery.flash_it.search.Result;

import java.util.List;

/**
 * Created by yon on 19/08/16.
 */
public class SearchResultsEvent {
    private List<Result> resultList;
    public SearchResultsEvent(List<Result> resultList) {
        this.resultList = resultList;
    }
    public List<Result> getResultList() {
        return resultList;
    }
}
