package com.flashitdelivery.flash_it.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flashitdelivery.flash_it.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.timessquare.CalendarPickerView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import xyz.sahildave.widget.SearchViewLayout;

public class PickupInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SearchViewLayout searchViewLayout;
    private ReverseGeocodingTask reverseGeocodingTask;
    private SmartLocation.LocationControl locationControl;
    private SlidingUpPanelLayout slidingPaneLayout;
    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private Marker cameraPinLocationMarker;
    private Circle currentLocationCircle;
    private ImageView transparentImageMap;
    private ImageView transparentImageSearch;
    private TextView locationName;
    private TextView locationHint;
    private Button doneButton;
    CalendarPickerView calendar;
    private static final String CURRENT_LOCATION = "Current Location";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 10;
    private static final float DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_info);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today).inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        doneButton = (Button) findViewById(R.id.done);

        searchViewLayout = (SearchViewLayout)findViewById(R.id.search_view_container);
        searchViewLayout.setEnabled(false);
        searchViewLayout.setCollapsedHint("Find Location");
        transparentImageSearch = (ImageView)findViewById(R.id.transparent_image_search);
        final Activity thisActivity = this;
        transparentImageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViewLayout.requestDisallowInterceptTouchEvent(true);
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(thisActivity);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        });

        locationName = (TextView) findViewById(R.id.location_name);
        locationHint = (TextView) findViewById(R.id.card_hint);
        transparentImageMap = (ImageView) findViewById(R.id.transparent_image_map);

        locationControl = SmartLocation.with(getApplicationContext()).location()
        .config(new LocationParams.Builder().setAccuracy(LocationAccuracy.HIGH).setInterval(5000).setDistance(0f).build());
        locationControl.start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (googleMap != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (currentLocationCircle == null) {
                        addLocationCircle(googleMap, latLng);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    }
                    else {
                        currentLocationCircle.remove();
                        addLocationCircle(googleMap, latLng);
                    }
                    if (cameraPinLocationMarker == null) {
                        addCameraPinMarker(googleMap, latLng, CURRENT_LOCATION);
                        getPinLocation();
                    }
                }
            }
        });

        setupMapFragment();
        setupSwipeUpLayout();

        doneButton.setEnabled(false);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnResult();
            }
        });
    }
    private void returnResult() {
        Intent intent = new Intent();
        intent.putExtra("ADDRESS", locationName.getText().toString());
        LatLng latLng = googleMap.getCameraPosition().target;
        intent.putExtra("LAT", latLng.latitude);
        intent.putExtra("LNG", latLng.longitude);
        DateFormat df = new SimpleDateFormat(getString(R.string.simple_date_format));
        ArrayList<String> dateStringList = new ArrayList<>();
        for (Date d : calendar.getSelectedDates()) {
            String dString = df.format(d);
            dateStringList.add(dString);
        }
        intent.putStringArrayListExtra("DATES", dateStringList);
        setResult(RESULT_OK, intent);
        finish();
    }
    private void getPinLocation() {
        if (reverseGeocodingTask != null && reverseGeocodingTask.getStatus() == AsyncTask.Status.RUNNING) {
                                reverseGeocodingTask.cancel(true);
        }
        reverseGeocodingTask = new ReverseGeocodingTask(getApplicationContext());
        reverseGeocodingTask.execute(googleMap.getCameraPosition().target);
    }
    private void addCameraPinMarker(GoogleMap googleMap, LatLng latLng, String title) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pin_color);
        cameraPinLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(latLng).title(title).zIndex(999));
    }
    private void addLocationCircle(GoogleMap googleMap, LatLng latLng) {
        currentLocationCircle = googleMap.addCircle(new CircleOptions().center(latLng).radius(10).fillColor(Color.GRAY).strokeWidth(0));
    }
    private void setupMapFragment() {
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void setupSwipeUpLayout() {
        slidingPaneLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        transparentImageMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                slidingPaneLayout.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        slidingPaneLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                    locationHint.setVisibility(View.GONE);
                } else {
                    locationHint.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationControl.stop();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (!locationControl.state().isGpsAvailable()) {
            Snackbar.make(transparentImageMap, R.string.gps_unavailable, Snackbar.LENGTH_LONG).show();
        }
        findViewById(R.id.centerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationControl.getLastLocation() != null && cameraPinLocationMarker != null) {
                    LatLng latLng = new LatLng(locationControl.getLastLocation().getLatitude(), locationControl.getLastLocation().getLongitude());
                    float newZoom = googleMap.getCameraPosition().zoom < DEFAULT_ZOOM ? DEFAULT_ZOOM : googleMap.getCameraPosition().zoom;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, newZoom));
                    cameraPinLocationMarker.setPosition(latLng);
                    getPinLocation();
                }
            }
        });
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (cameraPinLocationMarker != null) {
                    cameraPinLocationMarker.setPosition(googleMap.getCameraPosition().target);
                    doneButton.setEnabled(false);
                    locationName.setTextColor(getResources().getColor(R.color.flashIt_grey));
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getPinLocation();
                        }
                    }, 2000);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (cameraPinLocationMarker != null) {
                    cameraPinLocationMarker.setPosition(place.getLatLng());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    locationName.setText(place.getName());
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Snackbar.make(transparentImageMap, R.string.place_search_error, Snackbar.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        Context mContext;

        public ReverseGeocodingTask(Context context){
            super();
            mContext = context;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;

            List<Address> addresses = null;
            String addressText="";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);

                addressText = String.format(getString(R.string.format_address),
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "");
            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            doneButton.setEnabled(true);
            locationName.setTextColor(getResources().getColor(R.color.black));
            if (addressText.isEmpty()) {
                Location lastLocation = locationControl.getLastLocation();
                locationName.setText(String.format(getString(R.string.format_coordinates), lastLocation.getLatitude(), lastLocation.getLongitude()));
            }
            else {
                locationName.setText(addressText);
            }
        }
    }
}
