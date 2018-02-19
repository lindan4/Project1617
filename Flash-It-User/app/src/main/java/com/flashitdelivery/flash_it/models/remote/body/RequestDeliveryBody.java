package com.flashitdelivery.flash_it.models.remote.body;

import java.util.List;

/**
 * Created by yon on 16/06/16.
 */
public class RequestDeliveryBody {
    int availability_period_id;
    String buyer_uid;
    String checklist;

    public RequestDeliveryBody(int availability_period_id, List<String> checklist, String buyer_uid) {
        this.availability_period_id = availability_period_id;
        this.checklist = new String();
        for (String ci : checklist) {
            this.checklist += ci + '\n';
        }
        this.buyer_uid = buyer_uid;
    }
}
