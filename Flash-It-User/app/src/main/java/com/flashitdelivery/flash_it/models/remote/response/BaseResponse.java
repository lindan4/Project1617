package com.flashitdelivery.flash_it.models.remote.response;

/**
 * Created by yon on 13/04/16.
 */
public abstract class BaseResponse {
    public transient boolean networkError;
    public transient boolean serverError;
    public transient boolean expiredToken;
}
