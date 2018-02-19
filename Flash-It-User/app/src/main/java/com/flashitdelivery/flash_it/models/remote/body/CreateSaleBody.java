package com.flashitdelivery.flash_it.models.remote.body;

/**
 * Created by Lindan on 2016-06-16.
 */
public class CreateSaleBody {
    private String type;
    private String user_id;
    private String receiver_id;
    private String name;
    private String description;
    private String status;
    private String ad_url;
    private String user_address;
    private String receiver_address;

    public CreateSaleBody(String user_id, String name, String description, String ad_url, String userAddress) {
        this.type = "S";
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.status = "E";
        this.ad_url = ad_url;
        this.user_address = userAddress;
    }
}
