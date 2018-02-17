package com.flashitdelivery.flash_it_partner.util.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flashitdelivery.flash_it_partner.R;

import java.util.ArrayList;

/**
 * Created by Lindan on 2016-07-31.
 */
public class DriverOnShiftOptionsHelper implements ActivityCompat.OnRequestPermissionsResultCallback
{

    private final String checkFor = "Requirements to check for on item";

    private String personPhoneNum;
    private MaterialDialog amidShiftDialog;
    private Activity activity;
    private ArrayList<String> checklist;

    private int CALL_REQUEST_CODE = 1;

    public DriverOnShiftOptionsHelper(Activity activity, String personPhoneNum, ArrayList<String> checklist)
    {
        amidShiftDialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.shift_option_list, false).build();

        this.setAmidShiftDialog(amidShiftDialog);
        this.setActivity(activity);
        this.setChecklist(checklist);
        this.setPersonPhoneNum(personPhoneNum);
    }

    public void showDriverShiftOptions()
    {
        amidShiftDialog.show();
        if (amidShiftDialog != null)
        {
            View shiftOptionsview = amidShiftDialog.getCustomView();

            RelativeLayout callBlock = (RelativeLayout) shiftOptionsview.findViewById(R.id.phoneBlock);
            callBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    call();
                }
            });

            RelativeLayout checklistBlock = (RelativeLayout) shiftOptionsview.findViewById(R.id.checklistBlock);
            checklistBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    DriverPreviewChecklistHelper checklistHelper = new DriverPreviewChecklistHelper(getActivity(), checkFor, getChecklist());
                    checklistHelper.inflate();
                }
            });

            RelativeLayout returnBlock = (RelativeLayout) shiftOptionsview.findViewById(R.id.returnBlock);
            returnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    amidShiftDialog.dismiss();
                }
            });
        }

    }

    public void setAmidShiftDialog(MaterialDialog amidShiftDialog)
    {
        this.amidShiftDialog = amidShiftDialog;
    }

    public MaterialDialog getAmidShiftDialog()
    {
        return this.amidShiftDialog;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public Activity getActivity()
    {
        return this.activity;
    }

    public ArrayList<String> getChecklist()
    {
        return this.checklist;
    }

    public void setChecklist(ArrayList<String> checklist)
    {
        this.checklist = checklist;
    }

    public String getPersonPhoneNum()
    {
        return this.personPhoneNum;
    }

    public void setPersonPhoneNum(String personPhoneNum)
    {
        this.personPhoneNum = personPhoneNum;
    }

    private void call()
    {
        int CALL_ALLOWED = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        if (CALL_ALLOWED == PackageManager.PERMISSION_GRANTED)
        {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + getPersonPhoneNum()));
            getActivity().startActivity(callIntent);

        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        //Toast.makeText(getActivity(), "Press call again.", Toast.LENGTH_SHORT).show();
        call();
    }
}
