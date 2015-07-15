package com.example.arjuns.homework_03;

import android.app.Activity;
import android.content.Intent;
import android.widget.MediaController;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.widget.VideoView;

/**
 * Created by arjuns on 7/6/2015.
 */
public class DisplayVideo extends Activity {
    DisplayMetrics myDisplayMetrics;
    MediaController myMediaController;
    int height, width;
    String path;
    Intent displayMediaIntent;
    VideoView myDisplayVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_video_layout);

        //Referencing the UI components present in display_video_layout.xml
        myDisplayVideoView = (VideoView)findViewById(R.id.myGridVideoView);
        displayMediaIntent = getIntent();
        path = displayMediaIntent.getStringExtra("filePath");

        //Setting the video player
        myMediaController = new MediaController(this);
        myDisplayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(myDisplayMetrics);

        height = myDisplayMetrics.heightPixels;
        width = myDisplayMetrics.widthPixels;

        myDisplayVideoView.setMinimumWidth(width);
        myDisplayVideoView.setMinimumHeight(height);
        myDisplayVideoView.setVisibility(View.VISIBLE);
        myDisplayVideoView.setMediaController(myMediaController);
        myDisplayVideoView.setVideoPath(path);
        myDisplayVideoView.requestFocus();

        myDisplayVideoView.start();

    }
}
