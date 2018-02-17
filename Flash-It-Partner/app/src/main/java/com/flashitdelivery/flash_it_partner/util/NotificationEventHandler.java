package com.flashitdelivery.flash_it_partner.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flashitdelivery.flash_it_partner.event.NotificationEvent;
import com.flashitdelivery.flash_it_partner.event.ui.DropoffEvent;
import com.flashitdelivery.flash_it_partner.event.ui.PickupEvent;
import com.flashitdelivery.flash_it_partner.event.ui.ScanQREvent;
import com.flashitdelivery.flash_it_partner.event.ui.ShowSnackbarEvent;
import com.flashitdelivery.flash_it_partner.models.remote.TokenRespondBody;
import com.flashitdelivery.flash_it_partner.util.Helper.DriverPreviewChecklistHelper;
import com.flashitdelivery.flash_it_partner.util.api.APIClient;
import com.flashitdelivery.flash_it_partner.util.api.APIServiceGenerator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yon on 12/07/16.
 */
public class NotificationEventHandler {
    private final static String ACTION = "ACTION";
    public enum EventVar {
        ACTION,
        QR_SHOW,
        QR_SCAN,
        CHECKLIST,
        SNACKBAR,
        RESPOND,
        REQUEST_SENDER_ID,
        ITEM_NAME,
        CHECKLIST_STRING,
        IS_SALE,
        TRUE,
        FALSE,
        PICKUP,
        DROPOFF,
    }
    public static final Map<String, EventVar> eventVars;
    static {
        eventVars = new HashMap<>();
        eventVars.put(ACTION, EventVar.ACTION);
        eventVars.put("QR_SHOW", EventVar.QR_SHOW);
        eventVars.put("QR_SCAN", EventVar.QR_SCAN);
        eventVars.put("CHECKLIST", EventVar.CHECKLIST);
        eventVars.put("SNACKBAR", EventVar.SNACKBAR);
        eventVars.put("RESPOND", EventVar.RESPOND);
        eventVars.put("REQUEST_SENDER_ID", EventVar.REQUEST_SENDER_ID);
        eventVars.put("ITEM_NAME", EventVar.ITEM_NAME);
        eventVars.put("CHECKLIST_STRING", EventVar.CHECKLIST_STRING);
        eventVars.put("PICKUP", EventVar.PICKUP);
        eventVars.put("DROPOFF", EventVar.DROPOFF);
        eventVars.put("IS_SALE", EventVar.IS_SALE);
    }
    private static EventVar getVar(String key) {
        return eventVars.get(key);
    }

    private static ArrayList<String> parseStringToChecklist(String checklistString, String delimiter)
    {
        StringTokenizer tokenizer = new StringTokenizer(checklistString, delimiter);
        ArrayList<String> popList = new ArrayList<String>();

        while (tokenizer.hasMoreTokens())
        {
            popList.add(tokenizer.nextToken());
        }
        return popList;
    }

    public static void handle(final Activity activity, final Context context, final NotificationEvent event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final APIClient client = APIServiceGenerator.createService(APIClient.class);
                switch (getVar(event.data.get(ACTION))) {//getVar(event.data.get(ACTION))) {
                case CHECKLIST:
                    String requestSenderId = event.data.get("REQUEST_SENDER_ID");
                    String itemName = event.data.get("ITEM_NAME");
                    String title = requestSenderId + " has requested " + itemName;
                    String checkListString = event.data.get("CHECKLIST_STRING");
                    Log.i("ACTIONHELPER", "CHECKLIST");

                    ArrayList<String> checklist = NotificationEventHandler.parseStringToChecklist(checkListString, "\r");

                    DriverPreviewChecklistHelper helper = new DriverPreviewChecklistHelper(context, title, checklist);
                    helper.setOnPositiveCallback(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            Log.i("JWT_ACCEPT", event.data.get("JWT_ACCEPT"));
                            client.respondToken(new TokenRespondBody(event.data.get("JWT_ACCEPT"))).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                }
                            });
                        }
                    });
                    helper.setOnNegativeCallback(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            client.respondToken(new TokenRespondBody(event.data.get("JWT_DECLINE"))).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                }
                            });
                        }
                    });
                    helper.inflate();
                    break;
                    case SNACKBAR:
                    Log.i("GOTSNACKBAR", event.data.get("MESSAGE"));
                    EventBus.getDefault().post(new ShowSnackbarEvent(event.data.get("MESSAGE")));
                    break;
                    case PICKUP:
                        EventBus.getDefault().post(new ShowSnackbarEvent(event.data.get("PICKUP")));
                        EventBus.getDefault().post(new PickupEvent(event.data.get("PICKUP"), event.data.get("IS_SALE").equals("TRUE"),
                                event.data.get("JWT_VERIFY")));
                        break;
                    case QR_SCAN:
                        EventBus.getDefault().post(new ShowSnackbarEvent(event.data.get("SCAN QR")));
                        EventBus.getDefault().post(new ScanQREvent());
                        break;
                    case DROPOFF:
                        EventBus.getDefault().post(new DropoffEvent(event.data.get("DROPOFF"), event.data.get("JWT_VERIFY")));
        }
            }
        });
    }
}
