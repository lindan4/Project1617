package com.flashitdelivery.flash_it_partner.util.Helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.flashitdelivery.flash_it_partner.DummyModels.DummyDelivery;
import com.flashitdelivery.flash_it_partner.R;
import com.flashitdelivery.flash_it_partner.util.Interface.OnVerifiedListener;

import java.util.ArrayList;

/**
 * Created by Lindan on 2016-07-13.
 */
public class VerificationDialogHelper implements QRCodeReaderView.OnQRCodeReadListener {
    private final String VERIFY_SOMETHING = "Verify ";
    private final String ENSURE_RECEIVER_VALIDITY = "Verify receiver of ";


    private Activity activity;
    private Context context;
    private DummyDelivery dummyDelivery;
    private ArrayList<String> deliveryChecklist;

    private MaterialDialog verifyDialog;

    private boolean codeActivated;

    private boolean verified;
    private boolean itemMatchesText;

    private QRCodeReaderView qrView;
    private RelativeLayout inputCodeLayout;
    private TextView changeVeriMethod;

    private Button pressToValidate;
    private EditText qrCodeInput;
    private OnVerifiedListener onVerifiedListener;

    private MaterialDialog multiChoiceList;

    final private String checklistPrompt = "Ensure that the item satisfies these requirements";
    final private String DONE = "DONE";
    final private String CHECK_ALL = "CHECK ALL";


    //true if user is getting items and not delivering them yet.
    //false is user id delivering items
    private boolean fetching;

    public VerificationDialogHelper(Activity activity, DummyDelivery dummyDelivery, ArrayList<String> deliveryChecklist, boolean fetching) {
        this.setContext(activity);
        this.setDummyDelivery(dummyDelivery);
        this.setDeliveryChecklist(deliveryChecklist);
        this.setCodeActivated(false);
        this.setVerified(false);
        this.setFetching(fetching);


        if (getFetching()) {
            verifyDialog = new MaterialDialog.Builder(context)
                    .title(VERIFY_SOMETHING + dummyDelivery.getName())
                    .canceledOnTouchOutside(false)
                    .customView(R.layout.dialog_verify_delivery_qr, false).build();
        } else {
            verifyDialog = new MaterialDialog.Builder(context)
                    .title(ENSURE_RECEIVER_VALIDITY + dummyDelivery.getName())
                    .canceledOnTouchOutside(false)
                    .customView(R.layout.dialog_verify_delivery_qr, false).build();
        }


        View verifyDialogView = verifyDialog.getCustomView();
        changeVeriMethod = (TextView) verifyDialogView.findViewById(R.id.noCameraButton);

        qrView = (QRCodeReaderView) verifyDialogView.findViewById(R.id.qrCodeScanner);
        inputCodeLayout = (RelativeLayout) verifyDialogView.findViewById(R.id.qrCodeInput);
        pressToValidate = (Button) verifyDialogView.findViewById(R.id.pressToValidate);
        qrCodeInput = (EditText) verifyDialogView.findViewById(R.id.inputCode);

        pressToValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrCodeInput.getText().toString().length() == 0) {
                    qrCodeInput.setText("");
                } else {
                    validateCode(qrCodeInput.getText().toString());
                }

            }
        });


        changeVeriMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(getCodeActivated())) {
                    changeVeriMethod.setText(getContext().getResources().getString(R.string.loading_lower));
                    qrView.setVisibility(View.GONE);
                    inputCodeLayout.setVisibility(View.VISIBLE);
                    changeVeriMethod.setText(getContext().getResources().getString(R.string.scan_qr_instead));
                    setCodeActivated(true);
                } else {
                    changeVeriMethod.setText(getContext().getResources().getString(R.string.loading_lower));
                    inputCodeLayout.setVisibility(View.INVISIBLE);
                    qrView.setVisibility(View.VISIBLE);
                    changeVeriMethod.setText(getContext().getResources().getString(R.string.type_code));
                    setCodeActivated(false);
                }
            }
        });
    }

    public void inflate() {
        verifyDialog.show();
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public DummyDelivery getDummyDelivery() {
        return this.dummyDelivery;
    }

    public void setDummyDelivery(DummyDelivery dummyDelivery) {
        this.dummyDelivery = dummyDelivery;
    }

    public ArrayList<String> getDeliveryChecklist() {
        return this.deliveryChecklist;
    }

    public void setDeliveryChecklist(ArrayList<String> deliveryChecklist) {
        this.deliveryChecklist = deliveryChecklist;
    }

    public boolean getCodeActivated() {
        return this.codeActivated;
    }

    public void setCodeActivated(boolean codeActivated) {
        this.codeActivated = codeActivated;
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        validateCode(text);
    }

    @Override
    public void cameraNotFound() {
        qrView.setVisibility(View.GONE);
        inputCodeLayout.setVisibility(View.VISIBLE);
        changeVeriMethod.setText("");
        setCodeActivated(true);
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    public void validateCode(String code) {
        itemMatchesText = true;
        verifyDialog.dismiss();
        if (getFetching()) {
            inflateCheckableChecklist(getDeliveryChecklist());
        } else {
            this.setVerified(true);
        }


    }

    private void inflateCheckableChecklist(ArrayList<String> deliveryChecklist) {
        if (deliveryChecklist.isEmpty()) {
            setVerified(true);
        } else {
            multiChoiceList = new MaterialDialog.Builder(getContext())
                    .title(checklistPrompt)
                    .canceledOnTouchOutside(false)
                    .items(deliveryChecklist)
                    .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                            int size = dialog.getListView().getCount();
                            Integer[] checkedItems = dialog.getSelectedIndices();
                            if (checkedItems.length == size) {
                                Toast.makeText(getContext(), "We're good", Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    })
                    .neutralText(CHECK_ALL)
                    .positiveText(DONE)
                    .build();

            View neutral = multiChoiceList.getActionButton(DialogAction.NEUTRAL);
            View positive = multiChoiceList.getActionButton(DialogAction.POSITIVE);

            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    multiChoiceList.selectAllIndicies();
                }
            });

            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multiChoiceList.getSelectedIndices().length == multiChoiceList.getListView().getCount()) {
                        multiChoiceList.dismiss();
                        setVerified(true);
                    } else {
                        int selectedItems = multiChoiceList.getSelectedIndices().length;
                        int totalItems = multiChoiceList.getListView().getCount();
                        String issue = "Only " + selectedItems + " of " + totalItems + " requirements have been satisfied";

                        Toast.makeText(getContext(), issue, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            multiChoiceList.show();
        }
    }

    public void setOnVerifiedListener(OnVerifiedListener onVerifiedListener) {
        this.onVerifiedListener = onVerifiedListener;
    }

    public boolean getVerified() {
        return this.verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
        boolean previousValue = false;

        if ((previousValue != getVerified()) && (onVerifiedListener != null)) {
            this.onVerifiedListener.onVerified(getVerified());
        }
    }

    public boolean getFetching() {
        return this.fetching;
    }

    public void setFetching(boolean fetching) {
        this.fetching = fetching;
    }
}
