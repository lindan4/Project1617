package com.flashitdelivery.flash_it.models;

import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;

import java.util.ArrayList;

/**
 * Created by Lindan on 2016-08-16.
 */
public class Item
{

    final private String URLStart = "https://";

    private long backendId;
    private boolean isPrivate;
    private String itemName;
    private String itemDescription;
    private String itemOwner;
    private String ownerAddress;
    private double itemPrice;
    private String itemURL;
    private String itemImageURL;
    private ArrayList<String> tags;
    private ArrayList<AvailabiltyPeriodBody> availability;

    public Item(String itemName, String itemDescription, String itemOwner, String ownerAddress, double itemPrice , String itemURL, String imageURL,
                ArrayList<String> tags, ArrayList<AvailabiltyPeriodBody> availability)
    {
        this.setItemName(itemName);
        this.setItemDescription(itemDescription);
        this.setItemOwner(itemOwner);
        this.setOwnerAddress(ownerAddress);
        this.setItemPrice(itemPrice);
        this.setItemURL(itemURL);
        this.setItemImageURL(imageURL);
        this.setTags(tags);
        this.setAvailability(availability);

    }

    public long getBackendId() {
        return this.backendId;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public String getItemName()
    {
        return this.itemName;
    }

    public  String getItemDescription()
    {
        return this.itemDescription;
    }

    public String getItemOwner()
    {
        return this.itemOwner;
    }

    public String getOwnerAddress()
    {
        return this.ownerAddress;
    }

    public double getItemPrice()
    {
        return this.itemPrice;
    }

    public String getItemURL()
    {
        return this.itemURL;
    }

    public String getItemImageURL()
    {
        return this.itemImageURL;
    }

    public ArrayList<String> getTags()
    {
        return this.tags;
    }

    public ArrayList<AvailabiltyPeriodBody> getAvailability()
    {
        return this.availability;
    }

    public void setBackendId(long backendId) {
        this.backendId = backendId;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public void setItemDescription(String itemDescription)
    {
        this.itemDescription = itemDescription;
    }

    public void setItemOwner(String itemOwner)
    {
        this.itemOwner = itemOwner;
    }

    public void setOwnerAddress(String ownerAddress)
    {
        this.ownerAddress = ownerAddress;
    }


    public void setItemPrice(double itemPrice)
    {
        this.itemPrice = itemPrice;
    }

    public void setItemURL(String itemURL)
    {
        this.itemURL = itemURL;
    }

    public void setItemImageURL(String itemImageURL)
    {
        this.itemImageURL = itemImageURL;
    }

    public void setTags(ArrayList<String> tags)
    {
        this.tags = tags;
    }

    public void setAvailability(ArrayList<AvailabiltyPeriodBody> availability)
    {
        this.availability = availability;
    }


}
