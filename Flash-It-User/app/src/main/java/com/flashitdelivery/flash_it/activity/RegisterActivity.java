package com.flashitdelivery.flash_it.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.helpers.UserStateHelper;
import com.flashitdelivery.flash_it.models.remote.body.CreateDisplayUsernameBody;
import com.flashitdelivery.flash_it.util.APIClient;
import com.flashitdelivery.flash_it.util.APIServiceGenerator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText usernameEditText;
    private Button doneButton;

    @Override
    public void onBackPressed() {
        usernameEditText.clearFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameEditText = (TextInputEditText) findViewById(R.id.username);
        doneButton = (Button) findViewById(R.id.button_done);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                doneButton.setEnabled(charSequence.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        doneButton.setEnabled(false);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
                doneButton.setEnabled(false);
                apiClient.createDisplayUsername(new CreateDisplayUsernameBody(UserStateHelper.getUID(), usernameEditText.getText().toString()))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }
                                else {
                                    usernameEditText.setError("Username Taken");
                                }
                                doneButton.setEnabled(true);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                doneButton.setEnabled(true);
                            }
                        });
            }
        });
    }
}
