package com.flashitdelivery.flash_it_partner.util.Helper;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flashitdelivery.flash_it_partner.DummyModels.DummyDelivery;
import com.flashitdelivery.flash_it_partner.R;
import com.flashitdelivery.flash_it_partner.util.Interface.OnRequestPressListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Lindan on 2016-07-11.
 */
public class AvailableDeliveriesHelper
{
    private ArrayList<DummyDelivery> dummyDeliveryList;
    private ArrayList<LatLng> userLocations;
    private ArrayList<LatLng> receiverLocations;
    private Context context;

    private LatLng driverLatLngLocation;

    private Activity activity;
    private OnRequestPressListener onRequestPressListener;

    private MaterialDialog itemDialog;

    private boolean acceptPressed;

    final private String LEFT_BRACKET = "(";
    final private String RIGHT_BRACKET = ")";
    final private String KM = " km";

    final private double MILES_TO_KM = 1.6;

    public AvailableDeliveriesHelper(ArrayList<DummyDelivery> dummyDeliveryList, LatLng driverLatLngLocation, Activity activity)
    {
        ArrayList<LatLng> tempUserList = new ArrayList<LatLng>();
        ArrayList<LatLng> tempRecList = new ArrayList<LatLng>();

        this.setDummyDeliveryList(dummyDeliveryList);
        this.setDriverLatLngLocation(driverLatLngLocation);
        this.setActivity(activity);
        this.setContext(getActivity());

        for (int k = 0; k < dummyDeliveryList.size(); k = k + 1)
        {
            try
            {
                String userAddress = dummyDeliveryList.get(k).getUserAddress();
                tempUserList.add(convertAddressToCoordinates(userAddress));

                String receiverAddress = dummyDeliveryList.get(k).getReceiverAddress();
                tempRecList.add(convertAddressToCoordinates(receiverAddress));
            }
            catch (Exception e)
            {

            }
        }
        this.setUserLocations(tempUserList);
        this.setReceiverLocations(tempRecList);
        this.setAcceptPressed(false);
    }

    public void show()
    {
        double distance;
        itemDialog = new MaterialDialog.Builder(getContext()).title("Nearby item(s) available for delivery")
                .customView(R.layout.list_delivery_items, false)
                .titleColor(getActivity().getResources().getColor(R.color.flashItPartner_purple))
                .positiveText("ACCEPT")
                .negativeText("DECLINE")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        setAcceptPressed(true);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setAcceptPressed(false);
                    }
                })
                .show();

        View listDeliveryItems = itemDialog.getView();
        ArrayList<DummyDelivery> dummyDeliv = this.getDummyDeliveryList();
        ArrayList<LatLng> userLocations = getUserLocations();

        if (getDriverLatLngLocation() != null) {
            for (int i = 0; i < userLocations.size(); i = i + 1) {

                    LatLng singleUserDestination = userLocations.get(i);

                    //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    distance = getDistanceFromCurrent(getDriverLatLngLocation(), singleUserDestination);
                    String distanceFormat = String.format("%.2f", distance);


                    LinearLayout baseItemLayout = (LinearLayout) listDeliveryItems.findViewById(R.id.baseDeliveryList);

                    View itemDeliveryAndChecklist = LayoutInflater.from(getContext()).inflate(R.layout.item_delivery_and_checklist, null);
                    TextView deliveryName = (TextView) itemDeliveryAndChecklist.findViewById(R.id.deliveryName);
                    TextView relativeDistance = (TextView) itemDeliveryAndChecklist.findViewById(R.id.relativeDistance);
                    deliveryName.setText(dummyDeliveryList.get(i).getName());
                    relativeDistance.setText(LEFT_BRACKET + distanceFormat + KM + RIGHT_BRACKET);
                    //TextView dropDown = (TextView) itemDeliveryAndChecklist.findViewById(R.id.dropDownChecklist);
                    baseItemLayout.addView(itemDeliveryAndChecklist);

            }
        }
        else
        {
            Toast.makeText(getActivity(), "It is always null", Toast.LENGTH_LONG).show();
        }
    }

    private LatLng convertAddressToCoordinates(String address) throws Exception
    {

        GeoApiContext geoApiContext = new GeoApiContext().setApiKey(getContext().getResources().getString(R.string.google_direction_key));
        GeocodingResult[] geocodingResults = GeocodingApi.geocode(geoApiContext, address).await();
        while (geocodingResults.length == 0)
        {
            geocodingResults = GeocodingApi.geocode(geoApiContext, address).await();
        }

        LatLng mapLatMng = new LatLng(0, 0);


        if (geocodingResults.length > 0)
        {
            mapLatMng = new LatLng(geocodingResults[0].geometry.location.lat, geocodingResults[0].geometry.location.lng);
        }
        //Geocoder mapCoord = new Geocoder(getContext(), Locale.getDefault());
        //List<Address> mapAddresses = mapCoord.getFromLocationName(address, 1);

        return mapLatMng;
    }

    private double getDistanceFromCurrent(LatLng startLocation, LatLng endLocation)
    {
        int Radius = 6371;// radius of earth in Km
        double lat1 = startLocation.latitude;
        double lat2 = endLocation.latitude;
        double lon1 = startLocation.longitude;
        double lon2 = endLocation.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return (Radius * c) * MILES_TO_KM;
    }

    public ArrayList<DummyDelivery> getDummyDeliveryList()
    {
        return this.dummyDeliveryList;
    }

    public void setDummyDeliveryList(ArrayList<DummyDelivery> dummyDeliveryList)
    {
        this.dummyDeliveryList = dummyDeliveryList;
    }

    public Context getContext()
    {
        return this.context;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public void setDriverLatLngLocation(LatLng driverLatLngLocation)
    {
        this.driverLatLngLocation = driverLatLngLocation;
    }

    public LatLng getDriverLatLngLocation()
    {
        return this.driverLatLngLocation;
    }

    public Activity getActivity()
    {
        return  this.activity;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public ArrayList<LatLng> getUserLocations()
    {
        return this.userLocations;
    }

    public void setUserLocations(ArrayList<LatLng> userLocations)
    {
        this.userLocations = userLocations;
    }

    public ArrayList<LatLng> getReceiverLocations()
    {
        return this.receiverLocations;
    }

    public void setReceiverLocations(ArrayList<LatLng> receiverLocations)
    {
        this.receiverLocations = receiverLocations;
    }

    public boolean getAcceptPressed()
    {
        return this.acceptPressed;
    }

    public void setAcceptPressed(boolean acceptPressed)
    {
        boolean previousValue = false;
        this.acceptPressed = acceptPressed;

        if ((previousValue != getAcceptPressed()) && (this.onRequestPressListener != null))
        {
            this.onRequestPressListener.OnRequestPress(getAcceptPressed());
        }
    }

    public void setOnRequestPressListener(OnRequestPressListener onRequestPressListener)
    {
        this.onRequestPressListener = onRequestPressListener;
    }

}

