package com.flashitdelivery.flash_it_partner.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.flashitdelivery.flash_it_partner.DummyModels.DummyDelivery;
import com.flashitdelivery.flash_it_partner.R;
import com.flashitdelivery.flash_it_partner.event.NotificationEvent;
import com.flashitdelivery.flash_it_partner.event.ui.ShowSnackbarEvent;
import com.flashitdelivery.flash_it_partner.firebase.FirebaseMessagingHelper;
import com.flashitdelivery.flash_it_partner.util.Helper.AvailableDeliveriesHelper;
import com.flashitdelivery.flash_it_partner.util.Helper.DriverOnShiftOptionsHelper;
import com.flashitdelivery.flash_it_partner.util.Helper.NotificationHelper;


import com.flashitdelivery.flash_it_partner.util.Interface.OnRequestPressListener;
import com.flashitdelivery.flash_it_partner.util.Interface.OnVerifiedListener;
import com.flashitdelivery.flash_it_partner.util.NotificationEventHandler;
import com.flashitdelivery.flash_it_partner.util.Helper.VerificationDialogHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.GeocodingResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
{

    final private int ONE = 1;
    final private String LEFT_BRACKET = "(";
    final private String RIGHT_BRACKET = ")";
    final private String KM = " km";
    final private String destinationBlank = "Destination: \n";

    final private double MILES_TO_KM = 1.6;


    final private String verifyText = "Verify ";

    private GoogleMap driverGoogleMap;
    private MapView driverMapView;
    private int CAMERA_REQUEST_CODE;

    private TextView currentTaskText;
    private int taskCounter;

    final private String fetchTrue = "GET ITEM ";
    final private String fetchFalse = "DELIVER ITEM ";
    final private String OF = " OF ";
    final private String SPACE = " ";

    final private String END_OF_SHIFT = "Thank you for delivering with Flash It.";


    private ImageView refreshButton;

    private Button myProfile;

    private Button startShiftButton;
    private Button verifyItemButton;

    private AvailableDeliveriesHelper testHelper;

    private LatLng driverLatLngLocation;
    private LatLng currentDestination;

    private boolean stepTwo;
    private boolean shiftStarted;


    private RelativeLayout driverMainOptions;
    private ScrollView scrollMapDirectionsWrapper;
    private LinearLayout mapDirections;
    private RelativeLayout destinationBox;
    private TextView destinationAddressText;
    private ImageView deliveryTruckBlueInfo;

    private List<Step> stepsForDirections;
    private ArrayList<LatLng> directionPositionList;

    private VerificationDialogHelper checkItem;

    private Polyline polyline;


    public Activity activity = null;

    public GoogleApiClient googleApiClient;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private LocationRequest locationRequest;

    public Location lastLocation;
    public boolean requestingLocationUpdates;

    private boolean followUser;

    private boolean readyToSetPrevious;
    private boolean readyToSetImmediate;
    private boolean readyToSetFollowing;

    private Step step;

    private final double ONE_POINT_NINETY_FOUR = 1.94;
    private final double DISTANCE_TO_NEXT_DIRECTION = 0.02955;
    private final int DIRECTION_BOX_HEIGHT = 160;
    private final int SEVENTY_TWO = 72;
    private final double ONE_HUNDRED_AND_FOURTY_METRES = 0.14;
    private final int TOLERANCE = 55;

    private int directionOnFocus = 0;

    LatLng directionItemLatLng;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        FirebaseMessagingHelper.registerDevice(this, FirebaseInstanceId.getInstance().getToken());

        followUser = false;

        activity = this;

        //xaxaxa


        driverMapView = (MapView) findViewById(R.id.googleMapObject);
        driverMapView.onCreate(savedInstanceState);
        driverMapView.getMapAsync(this);

        getUserToEnableCameraUsage();

        if (googleApiClient == null)
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();


        refreshButton = (ImageView) findViewById(R.id.refreshButton);

        startShiftButton = (Button) findViewById(R.id.startShiftButton);
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);
        startShiftButton.startAnimation(in);

        myProfile = (Button) findViewById(R.id.profileButton);
        myProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent profileIntent = new Intent(getBaseContext(), ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        verifyItemButton = (Button) findViewById(R.id.verifyDeliveryButton);

        driverMainOptions = (RelativeLayout) findViewById(R.id.driverMainOptions);
        mapDirections = (LinearLayout) findViewById(R.id.mapDirections);
        scrollMapDirectionsWrapper = (ScrollView) findViewById(R.id.mapScrollViewWrapper);

        destinationBox = (RelativeLayout) findViewById(R.id.destinationBox);
        destinationBox.setVisibility(View.GONE);

        destinationAddressText = (TextView) findViewById(R.id.destinationAddress);
        deliveryTruckBlueInfo = (ImageView) findViewById(R.id.deliveryTruckInfo);


        currentTaskText = (TextView) findViewById(R.id.currentTaskText);
        taskCounter = 0;

        shiftStarted = false;


    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //Setting map starts here
        int LOCATION_ALLOWED = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (LOCATION_ALLOWED == PackageManager.PERMISSION_GRANTED)
        {
            googleMap.setMyLocationEnabled(true);
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick()
            {
                followUser = true;
                return false;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng)
            {
                followUser = false;

            }
        });

        driverMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    followUser = false;
                }
                return false;
            }
        });


        driverGoogleMap = googleMap;
    }

    public void setItemMarkers(ArrayList<DummyDelivery> deliveryItems)
    {
        driverGoogleMap.clear();
        for (int k = 0; k < deliveryItems.size(); k = k + 1)
        {
            try
            {
                DummyDelivery nearbyDeliveryItem = deliveryItems.get(k);
                LatLng userAddress = convertAddressToCoordinates(nearbyDeliveryItem.getUserAddress());
                MarkerOptions itemMarker = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.flash_it_custom_marker_52));
                itemMarker.title(nearbyDeliveryItem.getName());
                driverGoogleMap.addMarker(itemMarker.position(userAddress));
            }
            catch (Exception e)
            {

            }
        }

    }

    public void initializeDeliveries(LatLng lastLocation)
    {
        final ArrayList<DummyDelivery> dummyDeliveries= new ArrayList<DummyDelivery>();
        ArrayList<String> dummyChecklistOne = new ArrayList<String>();
        dummyChecklistOne.add("Good");
        dummyChecklistOne.add("Awesome");

        final ArrayList<String> dummyChecklistTwo = new ArrayList<String>();
        ArrayList<String> dummyChecklistThree = new ArrayList<>();


        final DummyDelivery deliveryOne = new DummyDelivery("HTC 10", "Awesome item", "yonnie", "lindan", "2 Fenton Road, Markham, ON" , "40 Audia Court, Concord, ON ", dummyChecklistOne);
        final DummyDelivery deliveryTwo = new DummyDelivery("Prada", "Awesome item", "yonnie", "lindan", "196 Aldergrove Dr, Markham, ON", "676 Westburne Dr, Vaughan, ON", dummyChecklistOne);

        dummyDeliveries.add(deliveryOne);
        dummyDeliveries.add(deliveryTwo);


        ArrayList<DummyDelivery> selectThreeNearbyDeliveries = calibrateObtainRelativeDistance(lastLocation, dummyDeliveries, 0);
        setItemMarkers(selectThreeNearbyDeliveries);

        testHelper = new AvailableDeliveriesHelper(selectThreeNearbyDeliveries, lastLocation, MainActivity.this);

        startShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //verifyItemButton.setVisibility(View.VISIBLE);
                testHelper.show();
                testHelper.setOnRequestPressListener(new OnRequestPressListener() {
                    @Override
                    public void OnRequestPress(boolean changedVariable)
                    {



                        Animation out = new AlphaAnimation(1.0f, 0.0f);
                        out.setDuration(1000);
                        startShiftButton.startAnimation(out);
                        startShiftButton.setVisibility(View.INVISIBLE);
                        startShiftButton.setClickable(false);

                        shiftStarted = true;
                        startFetchingDeliveries(testHelper.getDriverLatLngLocation(), testHelper.getDummyDeliveryList(), true);

                        RelativeLayout.LayoutParams driverOptionsParam = (RelativeLayout.LayoutParams) driverMainOptions.getLayoutParams();
                        driverOptionsParam.height = 0;
                        driverMainOptions.setLayoutParams(driverOptionsParam);

                        LinearLayout.LayoutParams wrapperParams = (LinearLayout.LayoutParams) scrollMapDirectionsWrapper.getLayoutParams();
                        wrapperParams.height = dpToPx(DIRECTION_BOX_HEIGHT);
                        scrollMapDirectionsWrapper.setLayoutParams(wrapperParams);
                    }
                });
            }
        });

    }

    private void startFetchingDeliveries(final LatLng origin, final ArrayList<DummyDelivery> dummyDeliveryUser, final boolean signal)
    {
        readyToSetPrevious = true;
        readyToSetImmediate = true;
        readyToSetFollowing = true;

        followUser = true;


        if (dummyDeliveryUser.isEmpty())
        {
            if (!(stepTwo))
            {
                taskCounter = 0;
                ArrayList<DummyDelivery> calibrateByReceiverDesintaion = calibrateObtainRelativeDistance(origin, testHelper.getDummyDeliveryList(), 1);
                startFetchingDeliveries(testHelper.getDriverLatLngLocation(), calibrateByReceiverDesintaion, false);
            }
            else
            {
                taskCounter = 0;
                polyline.remove();
                RelativeLayout.LayoutParams driverOptionsOriginal = (RelativeLayout.LayoutParams) driverMainOptions.getLayoutParams();
                driverOptionsOriginal.height = dpToPx(SEVENTY_TWO);

                verifyItemButton.setVisibility(View.INVISIBLE);

                RelativeLayout.LayoutParams scrollWrapperOriginal = (RelativeLayout.LayoutParams) scrollMapDirectionsWrapper.getLayoutParams();
                scrollWrapperOriginal.height = dpToPx(SEVENTY_TWO);

                driverMainOptions.setLayoutParams(driverOptionsOriginal);
                scrollMapDirectionsWrapper.setLayoutParams(scrollWrapperOriginal);

                destinationBox.setVisibility(View.GONE);

                startShiftButton.setVisibility(View.VISIBLE);
                currentTaskText.setText("");

                Toast.makeText(MainActivity.this, END_OF_SHIFT, Toast.LENGTH_LONG).show();
                shiftStarted = false;
                initializeDeliveries(testHelper.getDriverLatLngLocation());
            }
        }
        else
        {
            try
            {
                    String currentTask;
                    taskCounter = taskCounter + 1;
                    String singleUserLocationString;
                    if (signal)
                    {
                        currentTask = fetchTrue + taskCounter + OF + testHelper.getDummyDeliveryList().size() + SPACE + LEFT_BRACKET + dummyDeliveryUser.get(0).getName() + RIGHT_BRACKET;
                        stepTwo = false;
                        singleUserLocationString = dummyDeliveryUser.get(0).getUserAddress();
                        currentTaskText.setText(currentTask);

                    }
                    else
                    {
                        currentTask = fetchFalse + taskCounter + OF + testHelper.getDummyDeliveryList().size() + SPACE + LEFT_BRACKET + dummyDeliveryUser.get(0).getName() + RIGHT_BRACKET;
                        stepTwo = true;
                        singleUserLocationString = dummyDeliveryUser.get(0).getReceiverAddress();
                        currentTaskText.setText(currentTask);
                    }

                    final LatLng singleUserLocation = convertAddressToCoordinates(singleUserLocationString);
                    destinationBox.setVisibility(View.VISIBLE);
                    destinationAddressText.setText(destinationBlank + singleUserLocationString);
                    destinationAddressText.setOnClickListener(setDirectionItemOnClick(singleUserLocation));
                    deliveryTruckBlueInfo.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            DriverOnShiftOptionsHelper currentShiftOptions = new DriverOnShiftOptionsHelper(MainActivity.this, "+12894008780", dummyDeliveryUser.get(0).getChecklist());
                            currentShiftOptions.showDriverShiftOptions();

                        }
                    });
                    setCurrentDestination(singleUserLocation);
                    guideUserMap(testHelper.getDriverLatLngLocation(), singleUserLocation);

                    if (signal)
                    {
                        checkItem = new VerificationDialogHelper(MainActivity.this, dummyDeliveryUser.get(0), dummyDeliveryUser.get(0).getChecklist(), true);
                    }
                     else
                    {
                        checkItem = new VerificationDialogHelper(MainActivity.this, dummyDeliveryUser.get(0), dummyDeliveryUser.get(0).getChecklist(), false);
                    }

                    verifyItemButton.setText(MainActivity.this.getResources().getText(R.string.verify_delivery));
                    verifyItemButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            checkItem.inflate();
                            verifyItemButton.setText(MainActivity.this.getResources().getString(R.string.pending));
                            checkItem.setOnVerifiedListener(new OnVerifiedListener()
                            {
                                @Override
                                public void onVerified(boolean verified)
                                {
                                    guideUserMap(origin, singleUserLocation);
                                    ArrayList<DummyDelivery> cutList = new ArrayList<DummyDelivery>(dummyDeliveryUser.subList(1, dummyDeliveryUser.size()));
                                    ArrayList<DummyDelivery> calibrateNewList = calibrateObtainRelativeDistance(testHelper.getDriverLatLngLocation(), cutList, 0);
                                    verifyItemButton.setText(MainActivity.this.getResources().getString(R.string.verify_delivery));
                                    if (signal)
                                    {
                                        startFetchingDeliveries(testHelper.getDriverLatLngLocation(), calibrateNewList, true);
                                    }
                                    else
                                    {
                                        startFetchingDeliveries(testHelper.getDriverLatLngLocation(), calibrateNewList, false);
                                    }
                                }
                            });
                        }
                    });
            }
            catch (Exception e)
            {
            }
        }
    }

    private void guideUserMap(LatLng origin, LatLng currentDestination)
    {
        directionOnFocus = 0;
        try {
            GoogleDirection.withServerKey(getResources().getString(R.string.google_direction_key))
                    .from(origin)
                    .to(currentDestination)
                    .transportMode(TransportMode.DRIVING)
                    .avoid(AvoidType.TOLLS)
                    .unit(Unit.METRIC)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            //Toast.makeText(MainActivity.this, rawBody, Toast.LENGTH_LONG).show();
                            Log.i("i", rawBody);
                            String status = direction.getStatus();
                            if (status.equals(RequestResult.OK)) {
                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                final List<Step> listSteps = leg.getStepList();
                                stepsForDirections = leg.getStepList();

                                directionPositionList = leg.getDirectionPoint();
                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, getResources().getColor(R.color.flashItPartner_purple));


                                if (mapDirections.getChildCount() > 0)
                                {
                                    mapDirections.removeAllViews();
                                }
                                if (polyline != null)
                                {
                                    polyline.remove();
                                }
                                polyline = driverGoogleMap.addPolyline(polylineOptions);
                                // Do something

                                for (int j = 0; j < listSteps.size(); j = j + 1)
                                {
                                        step = listSteps.get(j);
                                        String stepAsString = step.getHtmlInstruction();
                                        String stepAsStringPlain = html2text(stepAsString);
                                        View singleDirectionItem = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_single_direction, null);
                                        singleDirectionItem.setId(j);
                                        directionItemLatLng = step.getStartLocation().getCoordination();
                                        singleDirectionItem.setTag(directionItemLatLng);


                                        singleDirectionItem.setOnClickListener(setDirectionItemOnClick(directionItemLatLng));

                                        //driverGoogleMap.addMarker(new MarkerOptions().position(directionItemLatLng));
                                        TextView singleDirectionText = (TextView) singleDirectionItem.findViewById(R.id.directionText);

                                        singleDirectionText.setText(stepAsStringPlain);
                                        mapDirections.addView(singleDirectionItem);
                                }
                            }
                            if (status.equals(RequestResult.NOT_FOUND)) {
                                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e)
        {

        }
    }

    //7/19 9.12
    public View.OnClickListener setDirectionItemOnClick(final LatLng moveToLocation)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                followUser = false;
                driverGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moveToLocation, 19));
            }
        };
    }

    public static String html2text(String html)
    {
        return Jsoup.parse(html).text();
    }

    private LatLng convertAddressToCoordinates(String address) throws Exception
    {
        GeoApiContext geoApiContext = new GeoApiContext().setApiKey(MainActivity.this.getResources().getString(R.string.google_direction_key));
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

        return mapLatMng;
    }


    // 0 for distance from driver to users
    // 1 for distance from driver to people receiving items
    public ArrayList<DummyDelivery> calibrateObtainRelativeDistance(LatLng currentPosition, ArrayList<DummyDelivery> listToCalibrateObtain, int signal)
    {
        ArrayList<DummyDelivery> finalList = new ArrayList<DummyDelivery>();
        if (listToCalibrateObtain.isEmpty())
        {


        }
        else
        {
            if (signal == 0)
            {
                try {

                    ArrayList<Double> distancesList = new ArrayList<Double>();
                    Map<Double, DummyDelivery> sortTempMap = new TreeMap<Double, DummyDelivery>();


                    for (int i = 0; i < listToCalibrateObtain.size(); i = i + 1) {
                        DummyDelivery delivery = listToCalibrateObtain.get(i);
                        String itemOwnerAddress = delivery.getUserAddress();
                        LatLng itemOwnerCoordinates = convertAddressToCoordinates(itemOwnerAddress);
                        double relativeDistance = getDistanceFromCurrent(currentPosition, itemOwnerCoordinates);
                        sortTempMap.put(relativeDistance, delivery);
                        distancesList.add(relativeDistance);
                    }
                    Collections.sort(distancesList);
                    if (distancesList.size() < 3) {
                        for (int j = 0; j < distancesList.size(); j = j + 1) {
                            Double getRelDis = distancesList.get(j);
                            finalList.add(sortTempMap.get(getRelDis));
                        }
                    } else {
                        for (int k = 0; k < 3; k = k + 1) {
                            Double getRelDis = distancesList.get(k);
                            finalList.add(sortTempMap.get(getRelDis));
                        }
                    }
                }
                catch (Exception e)
                {

                }
            }
            if (signal == 1)
            {

                try {

                    ArrayList<Double> distancesList = new ArrayList<Double>();
                    Map<Double, DummyDelivery> sortTempMap = new TreeMap<Double, DummyDelivery>();

                    for (int i = 0; i < listToCalibrateObtain.size(); i = i + 1)
                    {
                        DummyDelivery delivery = listToCalibrateObtain.get(i);
                        String itemOwnerAddress = delivery.getReceiverAddress();
                        LatLng itemOwnerCoordinates = convertAddressToCoordinates(itemOwnerAddress);
                        double relativeDistance = getDistanceFromCurrent(currentPosition, itemOwnerCoordinates);
                        sortTempMap.put(relativeDistance, delivery);
                        distancesList.add(relativeDistance);
                    }
                    Collections.sort(distancesList);
                    if (distancesList.size() <3)
                    {
                        for (int j = 0; j < distancesList.size(); j = j + 1)
                        {
                            Double getRelDis = distancesList.get(j);
                            finalList.add(sortTempMap.get(getRelDis));

                        }
                    }
                    else
                    {
                        for (int k = 0; k < 3; k = k + 1)
                        {
                            Double getRelDis = distancesList.get(k);
                            finalList.add(sortTempMap.get(getRelDis));
                        }
                    }
                }
                catch (Exception e)
                {

                }
            }
        }
        return finalList;
    }

    //Credit to http://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2
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

        return Radius * c * MILES_TO_KM;
    }

    private void getUserToEnableCameraUsage() {
        int CAMERA_ALLOWED = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

        if (CAMERA_ALLOWED != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.enable_camera), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length == ONE) {
            } else {
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onStart()
    {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public final void onDestroy()
    {
        driverMapView.onDestroy();
        super.onDestroy();
    }

    //http://stackoverflow.com/questions/8295986/how-to-calculate-dp-from-pixels-in-android-programmatically
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public final void onLowMemory()
    {
        driverMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public final void onPause()
    {
        driverMapView.onPause();
        stopLocationUpdates();
        super.onPause();
    }


    @Override
    public final void onResume()
    {
        driverMapView.onResume();
        super.onResume();

        if (googleApiClient.isConnected() & !(requestingLocationUpdates))
        {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop()
    {
        googleApiClient.disconnect();
        requestingLocationUpdates = false;
        super.onStop();

    }

    @Override
    public void onBackPressed()
    {
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void OnNotificationEvent(NotificationEvent event) {
        NotificationHelper notificationHelper = new NotificationHelper(this, getApplicationContext(), event.data.get("TITLE"), event.data.get("MESSAGE"));
        notificationHelper.show();
        Snackbar.make(findViewById(android.R.id.content), event.data.get("MESSAGE"), Snackbar.LENGTH_LONG).show();
    }

    public void setCurrentDestination(LatLng currentDestination)
    {
        this.currentDestination = currentDestination;
    }

    public LatLng getCurrentDestination()
    {
        LatLng current;
        if (this.currentDestination == null)
        {
            current = driverLatLngLocation;
        }
        else
        {
            current = this.currentDestination;
        }
        return current;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onNotificationEventReceived(NotificationEvent event) {
        NotificationEventHandler.handle(this, this, event);
    }
    @SuppressWarnings("unused")
    @Subscribe
    public void onShowSnackbarEvent(ShowSnackbarEvent event) {
        Snackbar.make(findViewById(android.R.id.content), event.message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        initializeCurrentLocation();
    }

    private void initializeCurrentLocation()
    {
        int LOCATION_ALLOWED = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (LOCATION_ALLOWED == PackageManager.PERMISSION_GRANTED)
        {
            createLocationRequest();
            startLocationUpdates();

            //Toast.makeText(MainActivity.this, "Got last location", Toast.LENGTH_SHORT).show();

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null)
            {
                initializeDeliveries(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                driverGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 16));
            }
            else
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                initializeCurrentLocation();
            }
        }
    }

    protected void createLocationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> pendingResult = LocationServices
                .SettingsApi
                .checkLocationSettings(googleApiClient, locationSettingsRequestBuilder.build());

        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
            {
                Status result = locationSettingsResult.getStatus();
                if (result.getStatusCode() == LocationSettingsStatusCodes.SUCCESS)
                {
                    requestingLocationUpdates = true;
                    //Toast.makeText(MainActivity.this, "Gucci", Toast.LENGTH_SHORT).show();
                }

                if (result.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
                {
                    requestingLocationUpdates = false;
                    Toast.makeText(MainActivity.this, "Please enable location services", Toast.LENGTH_SHORT).show();
                }
                if (result.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE)
                {
                    requestingLocationUpdates = false;
                    Toast.makeText(MainActivity.this, "App cannot access settings", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    protected void startLocationUpdates()
    {
        int LOCATION_ALLOWED = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (LOCATION_ALLOWED == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(final Location location)
    {
        testHelper.setDriverLatLngLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        //Toast.makeText(MainActivity.this, "" + followUser, Toast.LENGTH_SHORT).show();
        if (shiftStarted)
        {
            if (followUser)
            {
                driverGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
                //driverGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
            }
            else
            {
                //Toast.makeText(MainActivity.this, "Something is setting it to false", Toast.LENGTH_SHORT).show();
                driverGoogleMap.stopAnimation();
            }

            if (directionPositionList != null)
            {
                boolean onPath = PolyUtil.isLocationOnPath(testHelper.getDriverLatLngLocation(), directionPositionList, true, TOLERANCE);

                if (!(onPath))
                {
                    Toast.makeText(MainActivity.this, "Recalculating", Toast.LENGTH_SHORT).show();
                    guideUserMap(testHelper.getDriverLatLngLocation(), getCurrentDestination());
                }
            }

            //Still need to test it out
            if (mapDirections != null)
            {
                View prevDirection = mapDirections.findViewById(directionOnFocus - 1);
                View immediateDirection = mapDirections.findViewById(directionOnFocus);
                View followingDirection = mapDirections.findViewById(directionOnFocus + 1);

                //prevDirection
                if (prevDirection != null)
                {
                    TextView prevText = (TextView) prevDirection.findViewById(R.id.directionText);
                    prevText.setTypeface(null, Typeface.NORMAL);
                    LatLng prevLatLng = (LatLng) prevDirection.getTag();
                    if (getDistanceFromCurrent(testHelper.getDriverLatLngLocation(), prevLatLng) < DISTANCE_TO_NEXT_DIRECTION)
                    {
                        if (readyToSetPrevious)
                        {
                            directionOnFocus = directionOnFocus - 1;
                            readyToSetPrevious = false;
                        }
                    }
                    else
                    {
                        readyToSetPrevious = true;
                    }
                }

                //immediateDirection
                if (immediateDirection != null)
                {
                    TextView immediateText = (TextView) immediateDirection.findViewById(R.id.directionText);
                    immediateText.setTypeface(null, Typeface.BOLD);

                    scrollMapDirectionsWrapper.scrollTo(0, immediateDirection.getTop());

                }


                //followingDirection
                if (followingDirection != null)
                {
                    TextView followingText = (TextView) followingDirection.findViewById(R.id.directionText);
                    followingText.setTypeface(null, Typeface.NORMAL);
                    LatLng followingLatLng = (LatLng) followingDirection.getTag();
                    if (getDistanceFromCurrent(testHelper.getDriverLatLngLocation(), followingLatLng) < DISTANCE_TO_NEXT_DIRECTION)
                    {
                        if (readyToSetFollowing)
                        {
                            directionOnFocus = directionOnFocus + 1;
                            readyToSetFollowing = false;
                        }
                    }
                    else
                    {
                        readyToSetFollowing = true;
                    }
                }

                //Toast.makeText(MainActivity.this, check.getText().toString(), Toast.LENGTH_SHORT).show();
            }
            //End of portion that needs to be tested out

            if (getDistanceFromCurrent(testHelper.getDriverLatLngLocation(), getCurrentDestination()) < ONE_HUNDRED_AND_FOURTY_METRES)
            {
                //Toast.makeText(MainActivity.this, "" + getDistanceFromCurrent(testHelper.getDriverLatLngLocation(), getCurrentDestination()), Toast.LENGTH_SHORT).show();
                verifyItemButton.setVisibility(View.VISIBLE);
            }
            else
            {
                verifyItemButton.setText(MainActivity.this.getResources().getText(R.string.verify_delivery));
                verifyItemButton.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            refreshButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!(shiftStarted))
                    {
                        Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.reassigning_deliveries), Toast.LENGTH_SHORT).show();
                        driverGoogleMap.clear();
                        initializeDeliveries(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.cannot_refresh), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        //Toast.makeText(MainActivity.this, "It's working", Toast.LENGTH_SHORT).show();
    }
    protected void stopLocationUpdates()
    {
        if (googleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
            requestingLocationUpdates = false;

        }

    }

}

