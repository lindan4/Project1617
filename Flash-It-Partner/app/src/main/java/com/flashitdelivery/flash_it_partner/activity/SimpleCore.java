package com.flashitdelivery.flash_it_partner.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.flashitdelivery.core.event.QRFinishedEvent;
import com.flashitdelivery.flash_it_partner.R;
import com.flashitdelivery.flash_it_partner.event.NotificationEvent;
import com.flashitdelivery.flash_it_partner.event.ui.DropoffEvent;
import com.flashitdelivery.flash_it_partner.event.ui.PickupEvent;
import com.flashitdelivery.flash_it_partner.event.ui.ScanQREvent;
import com.flashitdelivery.flash_it_partner.event.ui.ShowSnackbarEvent;
import com.flashitdelivery.flash_it_partner.models.remote.TokenRespondBody;
import com.flashitdelivery.flash_it_partner.models.remote.UsernameBody;
import com.flashitdelivery.flash_it_partner.util.NotificationEventHandler;
import com.flashitdelivery.flash_it_partner.util.api.APIClient;
import com.flashitdelivery.flash_it_partner.util.api.APIServiceGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleCore extends AppCompatActivity {

    private MaterialListView mListView;
    private final String START_SHIFT = "START_SHIFT";
    private final String VERIFY_PICKUP = "VERIFY_PICKUP";
    private final String VERIFY_DROPOFF = "VERIFY_DROPOFF";
    private final String PICKUP = "PICKUP";

    private Card startShiftCard;
    private String verifyPickupToken;
    private String verifyDropoffToken;
    private APIClient apiClient;
    private String userId;
    private MaterialDialog qrDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_core);
        EventBus.getDefault().register(this);
        mListView = (MaterialListView)findViewById(R.id.materialList);

        apiClient = APIServiceGenerator.createService(APIClient.class);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.user_id_prefs), Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");

        startShiftCard = new Card.Builder(this)
                .setTag("START_SHIFT")
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_small_image_card)
                .setTitle("START SHIFT")
                .setDescription("Press to start")
                .endConfig()
                .build();

        mListView.getAdapter().add(startShiftCard);

        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(Card card, int position) {
                if (card.getTag().equals(START_SHIFT)) {
                    apiClient.startShift(new UsernameBody(userId)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
                else if (card.getTag().equals(VERIFY_PICKUP)) {
                    apiClient.respondToken(new TokenRespondBody(verifyPickupToken)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
                else if (card.getTag().equals(VERIFY_DROPOFF)) {
                    apiClient.respondToken(new TokenRespondBody(verifyDropoffToken)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onItemLongClick(Card card, int position) {

            }
        });
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void OnNotificationEvent(NotificationEvent event)
    {
        NotificationEventHandler.handle(this, this, event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onShowSnackbarEvent(ShowSnackbarEvent event) {
        Snackbar.make(findViewById(R.id.mainView), event.message, Snackbar.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPickupEvent(PickupEvent event) {
        Snackbar.make(findViewById(R.id.mainView), event.address, Snackbar.LENGTH_SHORT).show();
        mListView.getAdapter().clearAll();
        Card pickupCard = new Card.Builder(this)
                .setTag(PICKUP)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_small_image_card)
                .setTitle("Pickup (" + (event.isSale ? "SALE" : "PERSONAL") + ")")
                .setDescription(event.address)
                .endConfig()
                .build();
        mListView.getAdapter().add(pickupCard);
        Card verifyCard = new Card.Builder(this)
                .setTag(VERIFY_PICKUP)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_small_image_card)
                .setTitle("Verify Pickup")
                .setDescription("press to verify")
                .endConfig()
                .build();
        mListView.getAdapter().add(verifyCard);
        verifyPickupToken = event.respondToken;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDropoffEvent(DropoffEvent event) {
        Log.i("DROPOFFTOKEN", event.respondToken);
        Snackbar.make(findViewById(R.id.mainView), event.address, Snackbar.LENGTH_SHORT).show();
        mListView.getAdapter().clearAll();
        Card pickupCard = new Card.Builder(this)
                .setTag(PICKUP)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_small_image_card)
                .setTitle("Dropoff")
                .setDescription(event.address)
                .endConfig()
                .build();
        mListView.getAdapter().add(pickupCard);
        Card verifyCard = new Card.Builder(this)
                .setTag(VERIFY_DROPOFF)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_small_image_card)
                .setTitle("Verify dropoff")
                .setDescription("press to verify")
                .endConfig()
                .build();
        mListView.getAdapter().add(verifyCard);
        verifyDropoffToken = event.respondToken;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onQRSCanEvent(ScanQREvent event) {
        qrDialog = new MaterialDialog.Builder(this)
                .title("Scan User QR")
                .customView(R.layout.dialog_scanqr, false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .build();
        qrDialog.show();
    }
    @SuppressWarnings("unused")
    @Subscribe
    public void onQRFinishedEvent(QRFinishedEvent event) {
        qrDialog.dismiss();
        apiClient.respondToken(new TokenRespondBody(event.text)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
