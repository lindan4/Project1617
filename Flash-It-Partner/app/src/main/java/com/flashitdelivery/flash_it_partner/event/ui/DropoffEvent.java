package com.flashitdelivery.flash_it_partner.event.ui;

/**
 * Created by yon on 14/07/16.
 */
public class DropoffEvent {
    public String address;
    public String respondToken;

    public DropoffEvent(String address, String respondToken) {
        this.address = address;
        this.respondToken = respondToken;
    }
}
