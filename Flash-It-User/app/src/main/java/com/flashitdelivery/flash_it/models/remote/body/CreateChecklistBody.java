package com.flashitdelivery.flash_it.models.remote.body;

/**
 * Created by Lindan on 2016-07-06.
 */
public class CreateChecklistBody
{
    private String user_id;
    private String checklist;
    private int delivery_id;
    private String request_sender_id;

    public CreateChecklistBody(String user_id, String request_sender_id, String checklist, int delivery_id)
    {
        this.user_id = user_id;
        this.request_sender_id = request_sender_id;
        this.checklist = checklist;
        this.delivery_id = delivery_id;

    }

}
