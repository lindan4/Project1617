package com.flashitdelivery.flash_it.models.remote.body;

/**
 * Created by yon on 05/04/16.
 */
public class UpdateUserReceiptEmailBody {
    String id_token;
    String email;

    public UpdateUserReceiptEmailBody(String idToken, String email) {
        this.id_token = idToken;
        this.email = email;
    }
}
