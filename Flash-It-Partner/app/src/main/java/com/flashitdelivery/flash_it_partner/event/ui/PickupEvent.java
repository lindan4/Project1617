package com.flashitdelivery.flash_it_partner.event.ui;

/**
 * Created by yon on 14/07/16.
 */
public class PickupEvent {
    public String address;
    public boolean isSale;
    public String respondToken;

    public PickupEvent(String address, boolean isSale, String respondToken) {
        this.address = address;
        this.isSale = isSale;
        this.respondToken = respondToken;
    }
}
