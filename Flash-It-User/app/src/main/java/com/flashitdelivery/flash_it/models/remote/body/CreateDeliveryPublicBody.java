package com.flashitdelivery.flash_it.models.remote.body;

import android.nfc.Tag;

import com.flashitdelivery.flash_it.models.remote.Address;

import java.util.List;

/**
 * Created by yon on 10/09/16.
 */
public class CreateDeliveryPublicBody {
    String user_id;
    String name;
    String description;
    Float price;
    String ad_url;
    List<Tag> tags;
    Address pickup_address;
    Address dropoff_address;

    public CreateDeliveryPublicBody(String ad_url, String description, String name, Address pickup_address, Float price, List<Tag> tags, String user_id) {
        this.ad_url = ad_url;
        this.description = description;
        this.name = name;
        this.pickup_address = pickup_address;
        this.price = price;
        this.tags = tags;
        this.user_id = user_id;
    }

    public static class Tag {
        public String text;
        public Tag(String text) {
            this.text = text;
        }
    }
}
