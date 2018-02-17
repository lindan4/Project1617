package com.flashitdelivery.flash_it_partner.util.Helper;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.view.View;

/**
 * Created by Lindan on 2016-07-04.
 */
public class NotificationHelper
{

    final private int ONE = 1;
    private View view;
    private Activity activity;
    private Context context;
    private String contentTitle;
    private String contentText;
    private int notifyId = 0;

    public NotificationHelper(Activity activity, Context context, String contentTitle, String contentText)
    {
        this.setActivity(activity);
        this.setContext(context);
        this.setContentTitle(contentTitle);
        this.setContentText(contentText);
        this.setNotifyId(notifyId);
    }

    public View getNotifyView()
    {
        return this.view;
    }

    public Activity getNotifyActivity()
    {
        return this.activity;
    }

    public Context getNotifyContext()
    {
        return this.context;
    }

    public String getContentTitle()
    {
        return this.contentTitle;
    }

    public String getContentText()
    {
        return this.contentText;
    }

    public int getNotifyId()
    {
        return this.notifyId;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public void setContentTitle(String contentTitle)
    {
        this.contentTitle = contentTitle;
    }

    public void setContentText(String contentText)
    {
        this.contentText = contentText;
    }

    public void setNotifyId(int notifyId)
    {
        this.notifyId = notifyId;
    }


    //Displays notification
    public void show()
    {
        NotificationManager notifyManager = (NotificationManager) this.getNotifyActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification.Builder(this.getNotifyContext()).setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(this.getContentTitle())
                .setContentText(this.getContentText()).getNotification();
        notifyManager.notify(getNotifyId(), notify);
        this.setNotifyId(getNotifyId() + ONE);

    }





}
