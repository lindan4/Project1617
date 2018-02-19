package com.flashitdelivery.flash_it.models.remote.body;

/**
 * Created by yon on 07/04/16.
 */
public class CreateDisplayUsernameBody {
    private String user_uid;
    private String display_username;

    public CreateDisplayUsernameBody(String idToken, String displayUsername) {
        this.user_uid = idToken;
        this.display_username = displayUsername;
    }
}
