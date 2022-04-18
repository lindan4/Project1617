package com.flashitdelivery.flash_it_partner.util.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flashitdelivery.flash_it_partner.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Lindan on 2016-07-09.
 */
public class DriverPreviewChecklistHelper {
    private Context context;
    private String title;
    private String checkListItemsAsString;
    private ArrayList<String> checkListItemsArrayList;
    private MaterialDialog.SingleButtonCallback onPositive;
    private MaterialDialog.SingleButtonCallback onNegative;

    final private String ACCEPT = "ACCEPT";
    final private String DECLINE = "DECLINE";


    public DriverPreviewChecklistHelper(Context context, String title, ArrayList<String> checkListItemsArrayList) {
        this.setContext(context);
        this.setTitle(title);
        this.setCheckListItemsArrayList(checkListItemsArrayList);

    }

    public String getTitle() {
        return this.title;
    }

    public ArrayList<String> getCheckListItemsArrayList() {
        return this.checkListItemsArrayList;
    }

    public Context getContext() {
        return this.context;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCheckListItemsAsString(String checkListItemsAsString) {
        this.checkListItemsAsString = checkListItemsAsString;
    }

    public void setCheckListItemsArrayList(ArrayList<String> checkListItemsArrayList) {
        this.checkListItemsArrayList = checkListItemsArrayList;
    }

    public void setOnPositiveCallback(MaterialDialog.SingleButtonCallback callback) {
        this.onPositive = callback;
    }

    public void setOnNegativeCallback(MaterialDialog.SingleButtonCallback callback) {
        this.onNegative = callback;
    }

    public void inflate() {
        MaterialDialog requestNotificationDialog = new MaterialDialog.Builder(context)
                .titleColor(this.getContext().getResources().getColor(R.color.flashItPartner_purple))
                .customView(R.layout.item_request_foreground_notification, false)
                .title(this.getTitle())
                .show();

        View checklistOutline = requestNotificationDialog.getCustomView();
        LinearLayout confirmChecklistLayout = (LinearLayout) checklistOutline.findViewById(R.id.notificationChecklist);
        ArrayList<String> checklistItem = this.getCheckListItemsArrayList();

        for (int i = 0; i < checklistItem.size(); i = i + 1) {
            View singleItem = LayoutInflater.from(context).inflate(R.layout.item_single_checklistitem, null);
            TextView singleItemText = (TextView) singleItem.findViewById(R.id.checklistItemString);
            singleItemText.setText(checklistItem.get(i));
            confirmChecklistLayout.addView(singleItem);
        }
    }
}
