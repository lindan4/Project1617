package com.flashitdelivery.flash_it.helpers;

/**
 * Created by yon on 15/09/16.
 */
public class UrlHelper {
    public static String getViewSetUrl(boolean isPrivate, long deliveryId) {
        return "delivery_" + (isPrivate ? "private" : "public") + "/" + deliveryId;
    }
}
