package com.flashitdelivery.flash_it_partner.event;

import java.util.Map;

/**
 * Created by yon on 16/06/16.
 */
public class NotificationEvent {
    public String from;
    public Map<String,String> data;
    public NotificationEvent(String from, Map<String, String> data) {
        this.from = from;
        this.data = data;
    }
}
