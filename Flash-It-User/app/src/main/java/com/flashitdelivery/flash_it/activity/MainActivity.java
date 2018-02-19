package com.flashitdelivery.flash_it.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.adapter.ViewPagerAdapter;
import com.flashitdelivery.flash_it.event.PhotoPickedEvent;
import com.flashitdelivery.flash_it.fragment.EnlistFragment;
import com.flashitdelivery.flash_it.fragment.HomeFragment;
import com.flashitdelivery.flash_it.fragment.MenuFragment;
import com.flashitdelivery.flash_it.fragment.NotificationFragment;
import com.flashitdelivery.flash_it.helpers.UserStateHelper;
import com.gigamole.navigationtabbar.ntb.NavigationTabBar;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;
import me.gujun.android.taggroup.TagGroup;
import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, EnlistFragment.OnFragmentInteractionListener, MenuFragment.OnFragmentInteractionListener, NotificationFragment.OnFragmentInteractionListener{

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private ViewPager viewPager;
    private NavigationTabBar navigationTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        setupViewPager(viewPager);
        setupNavigationTabBar(navigationTabBar, viewPager);
        setUpSmartLocation();
        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            // already signed in
//        } else {
//            // not signed in
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setProviders(
//                                    AuthUI.EMAIL_PROVIDER,
//                                    AuthUI.GOOGLE_PROVIDER,
//                                    AuthUI.FACEBOOK_PROVIDER)
//                            .build(),
//                    RC_SIGN_IN);
//        }
    }

    private void setUpSmartLocation() {
        SmartLocation.with(getApplicationContext()).location().config(LocationParams.BEST_EFFORT)
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("RESULT", "SOME_RESULT" + requestCode);
        if (requestCode == UserStateHelper.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                startActivity(new Intent(this, MainActivity.class));
                Log.i("USER_ID", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                Log.i("RESULTOK", "" + resultCode);
                finish();
            } else {
                Log.i("RESULTOK", "" + resultCode);
                // user is not signed in. Maybe just wait for the user to press
                // "sign in" again, or show a message
            }
        }
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                EventBus.getDefault().post(new PhotoPickedEvent(photos.get(0)));
            }
        }
    }

    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), getString(R.string.home));
        adapter.addFrag(new EnlistFragment(), getString(R.string.enlist));
        adapter.addFrag(new NotificationFragment(), getString(R.string.notifications));
        adapter.addFrag(new MenuFragment(), getString(R.string.menu));
        viewPager.setAdapter(adapter);
    }

    private void setupNavigationTabBar(NavigationTabBar navigationTabBar, ViewPager viewPager) {
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model(new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_home), getResources().getColor(R.color.flashIt_red))));
        models.add(new NavigationTabBar.Model(new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_tag), getResources().getColor(R.color.flashIt_red))));
        models.add(new NavigationTabBar.Model(new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_mailbox), getResources().getColor(R.color.flashIt_red))));
        models.add(new NavigationTabBar.Model(new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_menu), getResources().getColor(R.color.flashIt_red))));
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager);
    }

    public void hideNavigationTabBar() {
        navigationTabBar.hide();
        // disable viewpager
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    public void showNavigationTabBar() {
        navigationTabBar.show();
        // enable viewpager
        viewPager.setOnTouchListener(null);
    }
    /* clear focus from EditText on touching outside */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText || v instanceof TagGroup.TagView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


}

