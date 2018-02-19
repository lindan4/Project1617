package com.flashitdelivery.flash_it.models.remote.body;

import com.flashitdelivery.flash_it.models.remote.Address;

import java.util.List;

/**
 * Created by yon on 10/09/16.
 */
public class CreateDeliveryPrivateBody {
    String user_id;
    String receiver_id;
    String name;
    Float price;
    String description;
    String ad_url;
    Address pickup_address;
    Address dropoff_address;

    public CreateDeliveryPrivateBody(String ad_url, String user_id, Address pickup_address, String name, Float price, String description, String receiver_id) {
        this.ad_url = ad_url;
        this.user_id = user_id;
        this.pickup_address = pickup_address;
        this.name = name;
        this.price = price;
        this.description = description;
        this.receiver_id = receiver_id;
        this.dropoff_address = null;
    }
}
