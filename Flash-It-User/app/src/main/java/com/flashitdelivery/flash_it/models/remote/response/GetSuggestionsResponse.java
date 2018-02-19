package com.flashitdelivery.flash_it.models.remote.response;

import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yon on 19/08/16.
 */
public class GetSuggestionsResponse {
    public List<String> users;
    public List<Delivery> deliveries;
    public static class Delivery {
        public int id;
        public String owner;
        public String name;
        public String description;
        public String image;
        public String ad_url;
        public float price;
        public ArrayList<String> tags;
        public ArrayList<AvailabiltyPeriodBody> availability;
    }
}
