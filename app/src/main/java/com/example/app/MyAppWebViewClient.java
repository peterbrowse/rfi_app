package com.example.app;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyAppWebViewClient extends WebViewClient {

//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//        if (Uri.parse(url).getHost().endsWith("bovered.co.uk")) {
//            return true;
//        }
//
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        view.getContext().startActivity(intent);
//        return true;
//    }
}