package com.teamcs.myanmarlive;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    String loading_message;
    private static ProgressDialog pd;
    // you save my day
    // https://www.monstertechnocodes.com/2018/07/how-to-enable-fullscreen-mode-in-any.html
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();


        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken onCreate",newToken);
                // we have to send to server with other credential
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "msg_subscribed";
                        if (!task.isSuccessful()) {
                            msg = "msg_subscribe_failed";
                        }
                        Log.d("sub ops ", msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        final WebView WV = (WebView) findViewById(R.id.webView);
        //final String URL = "file:///android_asset/index.html";
        final String URL = "https://mmsoftware100.com/tv/client/";

        WebSettings settings = WV.getSettings();

        WV.setWebViewClient(new Browser_home());
        WV.setWebChromeClient(new MyChrome());
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        WV.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36 Edge/12.0");
        WV.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WV.loadUrl(URL);




        loading_message = getString(R.string.loading_message);
        pd = ProgressDialog.show(this, "", loading_message,true);

       WV.setWebViewClient(new WebViewClient() {

           @Override
           public void onPageStarted(WebView view, String url, Bitmap favicon) {
               super.onPageStarted(view, url, favicon);
               if (!pd.isShowing()) {
                   pd.show();
               }
           }
           @Override
           public void onPageFinished(WebView view, String url) {
               if(pd!=null && pd.isShowing())
               {
                   pd.dismiss();
               }
           }
           public void onReceivedError(WebView webView, int i, String str, String str2) {
               webView.loadUrl("file:///android_asset/error.html");
               getSupportActionBar().show();
           }

       });
       WV.setWebChromeClient(new WebChromeClient(){
           public void onProgressChanged(WebView view, int progress) {
               pd.setMessage(loading_message+ " " +progress+" %");
           }
       });

        // ‌ads section

        mAdView = findViewById(R.id.adView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // interstitial ads
        // ca-app-pub-3940256099942544/1033173712
        mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        String intersialCode = getString(R.string.interstitial1);
        mInterstitialAd.setAdUnitId(intersialCode);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d("TAG", "The interstitial onAdFailedToLoad errorCode " + errorCode);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.d("TAG", "The interstitial onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("TAG", "The interstitial onAdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d("TAG", "The interstitial onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                // Load the next interstitial.
                Log.d("TAG", "The interstitial onAdClosed");
                // mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());


    }



    class Browser_home extends WebViewClient {

        Browser_home() {
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (!pd.isShowing()) {
                pd.show();
            }
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(pd!=null && pd.isShowing())
            {
                pd.dismiss();
            }

            setTitle(view.getTitle());
            super.onPageFinished(view, url);

        }


            public void onReceivedError(WebView webView, int i, String str, String str2) {
                webView.loadUrl("file:///android_asset/error.html");
                getSupportActionBar().show();
            }
    }


    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }
        public void onProgressChanged(WebView view, int progress) {
                pd.setMessage(loading_message+ " " +progress+" %");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean _doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (_doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }
        this._doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, getResources().getString(R.string.exit_greeting), Toast.LENGTH_SHORT).show();
        Snackbar.make(getWindow().getDecorView(), getResources().getString(R.string.exit_greeting), Snackbar.LENGTH_LONG).show();

        //WV.loadUrl("javascript:android_hello()");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                _doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
