package com.flashitdelivery.flash_it.models.remote.body;

/**
 * Created by yon on 30/06/16.
 */
public class GetMyDeliveriesBody {
    private String username;
    private boolean is_history_list;

    public GetMyDeliveriesBody(String username, boolean isHistory) {
        this.username = username;
        this.is_history_list = isHistory;
    }
}
