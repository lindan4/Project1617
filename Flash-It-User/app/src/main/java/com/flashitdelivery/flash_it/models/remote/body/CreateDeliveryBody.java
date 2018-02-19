package com.flashitdelivery.flash_it.models.remote.body;

/**
 * Created by Lindan on 2016-06-16.
 */
public class CreateDeliveryBody {
    private String type;
    private String user_id;
    private String receiver_id;
    private String name;
    private String description;
    private String status;
    private String ad_url;
    private String user_address;
    private String receiver_address;

    public CreateDeliveryBody(String user_id, String receiver_id, String name, String description, String userAddress, String receiverAddress) {
        this.type = "P";
        this.user_id = user_id;
        this.receiver_id = receiver_id;
        this.name = name;
        this.description = description;
        this.status = "E";
        this.ad_url = "";
        this.user_address = userAddress;
        this.receiver_address = receiverAddress;
    }

}
