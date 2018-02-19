package com.flashitdelivery.flash_it.event;

/**
 * Created by yon on 19/08/16.
 */
public class PhotoPickedEvent {
    public String photoUrl;
    public PhotoPickedEvent(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
