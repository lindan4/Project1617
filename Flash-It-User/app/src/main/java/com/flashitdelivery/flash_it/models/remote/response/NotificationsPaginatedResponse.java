package com.flashitdelivery.flash_it.models.remote.response;

import android.app.Notification;

import java.util.List;

/**
 * Created by yon on 16/09/16.
 */
public class NotificationsPaginatedResponse {
    public String count;
    public String next;
    public String previous;
    public List<Notification> results;

    public static class Notification {
        public int id;
        public int type;
        public int delivery_id;
        public String checklist;
        public String thumbnail;
        public String other_user_username;
        public TimeAgo time_ago;
    }

    public static class TimeAgo {
        public int hours;
        public int minutes;
        public int days;
    }
}
