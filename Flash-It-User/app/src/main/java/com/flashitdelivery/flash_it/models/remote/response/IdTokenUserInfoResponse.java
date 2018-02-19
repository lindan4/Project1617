package com.flashitdelivery.flash_it.models.remote.response;

/**
 * Created by yon on 12/04/16.
 */
public class IdTokenUserInfoResponse extends BaseResponse {
    public String picture;
    public String display_username;
    public String nickname;
    public boolean is_payment_activated;
    public boolean has_receipt_email;
    public boolean nickname_valid_username;
}
