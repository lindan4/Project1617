package com.flashitdelivery.flash_it.search;

import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;

import java.util.ArrayList;

/**
 * Created by yon on 19/08/16.
 */
public class Result {
    private ResultType resultType;
    private ResultData resultData;
    public Result(ResultType resultType, ResultData resultData) {
        this.resultType = resultType;
        this.resultData = resultData;
    }
    public static class ResultData {
    }

    public ResultType getResultType() {
        return resultType;
    }
    public ResultData getResultData() {
        return resultData;
    }
    public static class UserData extends ResultData {
        public String username;
        public UserData(String username) {
            this.username = username;
        }
    }
    public static class ItemData extends ResultData {
        public String itemOwner;
        public String itemName;
        public String itemImage;
        public String itemDesc;
        public String adUrl;
        public int deliveryId;
        public ArrayList<String> tags;
        public ArrayList<AvailabiltyPeriodBody> availability;
        public float price;
        public ItemData(int deliveryId, String itemOwner, String itemName, String itemImage, String itemDesc, String adUrl, float price, ArrayList<String> tags, ArrayList<AvailabiltyPeriodBody> availability) {
            this.itemOwner = itemOwner;
            this.deliveryId = deliveryId;
            this.itemName = itemName;
            this.itemImage = itemImage;
            this.itemDesc = itemDesc;
            this.adUrl = adUrl;
            this.price = price;
            this.tags = tags;
            this.availability = availability;
        }
    }
    public enum ResultType {
        User,
        Item
    }
}
