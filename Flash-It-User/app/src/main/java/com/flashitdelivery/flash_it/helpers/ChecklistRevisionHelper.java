package com.flashitdelivery.flash_it.helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.models.Item;
import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;
import com.flashitdelivery.flash_it.models.remote.body.RequestDeliveryBody;
import com.flashitdelivery.flash_it.util.APIClient;
import com.flashitdelivery.flash_it.util.APIServiceGenerator;

import java.util.ArrayList;

/**
 * Created by Lindan on 2016-09-05.
 */
public class ChecklistRevisionHelper
{

    private Activity activity;
    private Item item;
    private ArrayList<String> checklist;
    private MaterialDialog checklistDialog;
    private LinearLayout viewRec;

    private EditText input;

    public ChecklistRevisionHelper(Activity activity, final Item item)
    {
        this.setActivity(activity);
        this.setItem(item);
        this.setChecklist(new ArrayList<String>());
        this.setChecklistDialog(new MaterialDialog.Builder(activity).customView(R.layout.checklist_revision_view, false).positiveText(getActivity()
                .getResources().getString(R.string.request))
                .title(getActivity().getResources().getString(R.string.checklist_title))
                .titleColor(getActivity().getResources().getColor(R.color.flashIt_red))
                .neutralText(getActivity().getResources().getString(R.string.previous_text).toUpperCase())
                .neutralColor(getActivity().getResources().getColor(R.color.flashIt_red))
                .canceledOnTouchOutside(false)
                .positiveColor(getActivity().getResources().getColor(R.color.flashIt_red))
                .positiveText(getActivity().getResources().getString(R.string.finish).toUpperCase())
                .contentGravity(GravityEnum.CENTER)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .build());

        MaterialDialog checklistDialogCopy = this.getChecklistDialog();

        View customChecklist = checklistDialogCopy.getCustomView();
        viewRec = (LinearLayout) customChecklist.findViewById(R.id.checklistItems);
        TextView addBox = (TextView) customChecklist.findViewById(R.id.addButton);

        input = (EditText) customChecklist.findViewById(R.id.checklistTaskInput);
        addToList(getActivity().getResources().getString(R.string.base_checklist_item));

        addBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String textInput = input.getText().toString();
                if (textInput.isEmpty())
                {
                    input.setText("");
                }
                else
                {
                    addToList(textInput);
                    input.setText("");
                }
            }
        });

        input.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) && event.getAction() == KeyEvent.ACTION_DOWN) {
                addToList(input.getText().toString());
                input.setText("");
                return true;
            }
            return false;
        }
    });
    }

    public Activity getActivity()
    {
        return this.activity;
    }

    public Item getItem()
    {
        return this.item;
    }

    public ArrayList<String> getChecklist()
    {
        if (checklist.isEmpty()) {
            checklist.add(getActivity().getResources().getString(R.string.base_checklist_item));
        }
        return this.checklist;
    }

    public MaterialDialog getChecklistDialog()
    {
        return this.checklistDialog;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public void setChecklist(ArrayList<String> checklist)
    {
        this.checklist = checklist;
    }

    public void setChecklistDialog(MaterialDialog checklistDialog)
    {
        this.checklistDialog = checklistDialog;
    }

    public void inflate()
    {
        getChecklistDialog().show();
    }

    public void addToList(String lookFor)
    {
        if (viewRec != null)
        {
            if (lookFor.isEmpty())
            {

            }
            else
            {
                View checkView = LayoutInflater.from(getActivity()).inflate(R.layout.item_checklist_spec, null);
                TextView checkViewText = (TextView) checkView.findViewById(R.id.checklistTask);
                checkViewText.setText(lookFor);

                ImageView deleteButton = (ImageView) checkView.findViewById(R.id.deleteToTrash);
                deleteButton.setOnClickListener(removeChecklistItem(viewRec, checkView));

                viewRec.addView(checkView);
            }

        }

    }

    public View.OnClickListener removeChecklistItem(final LinearLayout parent, final View child)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                parent.removeView(child);
            }
        };
    }
}
