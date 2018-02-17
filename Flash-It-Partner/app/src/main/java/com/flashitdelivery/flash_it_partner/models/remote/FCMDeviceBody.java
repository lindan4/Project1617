package com.flashitdelivery.flash_it_partner.models.remote;

/**
 * Created by yon on 16/06/16.
 */
public class FCMDeviceBody {
    public FCMDeviceBody(String name, String registrationId, String deviceId, boolean active) {
        this.name = name;
        this.reg_id = registrationId;
        this.dev_id = deviceId;
        this.is_active = active;
    }
    String name;
    String reg_id;
    String dev_id;
    boolean is_active;
}
