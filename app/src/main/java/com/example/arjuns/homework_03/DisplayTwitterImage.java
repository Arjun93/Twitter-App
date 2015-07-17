package com.example.arjuns.homework_03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;


/**
 * Created by asanthan on 7/16/15.
 */
public class DisplayTwitterImage extends Activity{
    WebView myImageWebView;
    Intent urlFetcherIntent;
    String imageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_image_layout);
        myImageWebView = (WebView) findViewById(R.id.imageWebView);
        urlFetcherIntent = getIntent();
        imageURL = urlFetcherIntent.getStringExtra("ImageURL");
        Log.i("IMAGE URL",imageURL);
        myImageWebView.loadUrl(imageURL);
    }
}
