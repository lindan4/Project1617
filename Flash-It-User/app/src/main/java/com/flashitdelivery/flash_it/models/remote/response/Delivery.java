package com.flashitdelivery.flash_it.models.remote.response;

import com.flashitdelivery.flash_it.models.remote.Address;
import com.flashitdelivery.flash_it.models.remote.body.CreateDeliveryPublicBody;

import java.util.List;

/**
 * Created by yon on 02/06/16.
 */
public class Delivery {
    public int id;
    public String type;
    public String user_id;
    public String receiver_id;
    public String name;
    public String description;
    public String thumbnail;
    public String timestamp;
    public String ad_url;
    public double price;
    public List<CreateDeliveryPublicBody.Tag> tags;
}
