package com.flashitdelivery.flash_it.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flashitdelivery.flash_it.R;

/**
 * Created by Lindan on 2016-08-18.
 */
public class WebViewActivity extends AppCompatActivity
{
    private String LINK_START_SSL;
    private String LINK_START;
    private String HOMEPAGE;
    private String REDIRECT_ERROR_TO_SEARCH;


    private WebView webView;
    private EditText urlInput;

    private String urlErrorLink;

    private InputMethodManager imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        LINK_START_SSL = getString(R.string.link_start_ssl);
        LINK_START = getString(R.string.link_start);
        HOMEPAGE = getString(R.string.home_url);
        REDIRECT_ERROR_TO_SEARCH = getString(R.string.home_error_redirect);

        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        final TextView pageTitle = (TextView) findViewById(R.id.pageTitle);

        webView = (WebView) findViewById(R.id.externalWebView);
        webView.getSettings().setJavaScriptEnabled(true);

        boolean post = getIntent().getExtras().getBoolean(getString(R.string.post));

        Button postButton = (Button) findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent data = new Intent();
                data.putExtra("url", webView.getUrl());
                setResult(RESULT_OK, data);
                finish();
            }
        });
        if (post)
        {
            postButton.setVisibility(View.VISIBLE);
            webView.loadUrl(HOMEPAGE);
        }
        else
        {
            String adLink = getIntent().getExtras().getString(getString(R.string.ad_link));
            webView.loadUrl(adLink);
            postButton.setVisibility(View.GONE);
        }



        ImageView goToButton = (ImageView) findViewById(R.id.goToButton);
        goToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                goToLink(urlInput.getText().toString());
            }
        });

        urlInput = (EditText) findViewById(R.id.urlInput);
        urlInput.setSelectAllOnFocus(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        urlInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    goToLink(urlInput.getText().toString());
                    return true;
                }
                return false;
            }
        });

        webView.requestFocus();

        final ImageView goBack = (ImageView) findViewById(R.id.webviewBack);
        final ImageView goForward = (ImageView) findViewById(R.id.webviewForward);


        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                pageTitle.setText(getResources().getString(R.string.loading));
                urlInput.setText(url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            {
                super.onReceivedError(view, request, error);
                view.loadUrl(REDIRECT_ERROR_TO_SEARCH + urlErrorLink);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);

                pageTitle.setText(view.getTitle());

                if (webView.canGoBack())
                {
                    goBack.setImageDrawable(getResources().getDrawable(R.drawable.back_flashitred_50));
                    goBack.setClickable(true);
                    goBack.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            webView.goBack();
                        }
                    });
                }
                else
                {
                    goBack.setImageDrawable(getResources().getDrawable(R.drawable.back_gray_50));
                    goBack.setClickable(false);
                }

                if (webView.canGoForward())
                {
                    goForward.setImageDrawable(getResources().getDrawable(R.drawable.back_flashitred_50));
                    goForward.setClickable(true);
                    goForward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            webView.goForward();
                        }
                    });
                }
                else
                {
                    goForward.setImageDrawable(getResources().getDrawable(R.drawable.back_gray_50));
                    goForward.setClickable(false);
                }

            }
        });




        ImageView exitButton = (ImageView) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    private void goToLink(String url)
    {
        if (url.contains(LINK_START_SSL) || url.contains(LINK_START))
        {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            urlErrorLink = url;
            webView.loadUrl(url);
            urlInput.setText(url);
        }
        else
        {
            urlErrorLink = url;
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            webView.loadUrl(LINK_START_SSL + url);
            urlInput.setText(LINK_START_SSL + url);

        }

        Button postButton = (Button) findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent data = new Intent();
                data.putExtra("url", webView.getUrl());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
