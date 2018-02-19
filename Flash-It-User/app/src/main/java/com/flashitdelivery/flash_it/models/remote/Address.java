package com.flashitdelivery.flash_it.models.remote;

/**
 * Created by yon on 10/09/16.
 */
public class Address {
        String name;
        double lat;
        double lng;
        public Address(String name, double lat, double lng) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
        }
}
