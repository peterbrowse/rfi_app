package com.example.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.net.Uri;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.graphics.Bitmap;
import android.view.animation.TranslateAnimation;
import android.content.res.Configuration;

import java.io.File;


@SuppressWarnings("WrongConstant")
public class MainActivity extends Activity {

    private WebView mWebView;
    private boolean isRedirected;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        String agentModified = mWebView.getSettings().getUserAgentString().concat("WK_CREDS_APP");

        mWebView.getSettings().setUserAgentString(agentModified);

        // Use local resource
        // mWebView.loadUrl("file:///android_asset/www/index.html");

        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line
        //mWebView.getSettings().setAppCachePath("/data/data/"+ BuildConfig.APPLICATION_ID +"/cache");
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        if ( !isNetworkAvailable() ) { // loading offline
            mWebView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        } else { // loading online content
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        }

        //mWebView.loadUrl("http://bovered.co.uk/bwrfi/");

        mWebView.loadUrl("file:///android_asset/index.html");

        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setBackgroundColor(128);

        mWebView.setWebViewClient(new MyAppWebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, String url){
                Log.d("URL", "address: " + url);

                isRedirected = true;

                String url2 = "file:///android_asset/";
                // all links  with in ur site will be open inside the webview
                //links that start ur domain example(http://www.example.com/)
                if (url != null && url.startsWith(url2)){
                    return false;
                }
                // all links that points outside the site will be open in a normal android browser
                else  {
                    webview.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
            }

            public void onPageFinished(WebView view, String url) {
                WebView webview = (WebView) findViewById(R.id.activity_main_webview);

                ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);

                webview.setVisibility(0);

                bar.setVisibility(10);
                if (!isRedirected) {
                    TranslateAnimation animate = new TranslateAnimation(0, 0, webview.getHeight(), 0);
                    animate.setDuration(600);
                    animate.setFillAfter(true);
                    webview.startAnimation(animate);
                    webview.setVisibility(View.GONE);
                    isRedirected = true;
                }
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                WebView webview = (WebView) findViewById(R.id.activity_main_webview);

                ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);

                webview.setVisibility(10);

                bar.setVisibility(0);

                isRedirected = false;
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        });
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//            Log.d("Orientation", "onConfigurationChanged Landscape: " + mWebView.getWidth());
//
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//
//            Log.d("Orientation", "onConfigurationChanged Portrait: " + mWebView.getWidth());
//        }
//    }

    // Prevent the back-button from closing the app
    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}