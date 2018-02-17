package com.flashitdelivery.flash_it_partner.DummyModels;

import java.util.ArrayList;

/**
 * Created by Lindan on 2016-07-11.
 */
public class DummyDelivery
{
    private String name;
    private String description;
    private String user_id;
    private String receiver_id;
    private String userAddress;
    private String receiverAddress;
    private ArrayList<String> checklist;
    public DummyDelivery(String name, String description, String user_id, String receiver_id, String userAddress, String receiverAddress, ArrayList<String> checklist)
    {
        this.name = name;
        this.description = description;
        this.user_id = user_id;
        this.receiver_id = receiver_id;
        this.userAddress = userAddress;
        this.receiverAddress = receiverAddress;
        this.checklist = checklist;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getUser_id()
    {
        return this.user_id;
    }

    public String getReceiver_id()
    {
        return this.receiver_id;
    }

    public String getUserAddress()
    {
        return this.userAddress;
    }

    public String getReceiverAddress()
    {
        return this.receiverAddress;
    }

    public ArrayList<String> getChecklist()
    {
        return this.checklist;
    }

    public void setChecklist(ArrayList<String> checklist)
    {
        this.checklist = checklist;
    }
}


