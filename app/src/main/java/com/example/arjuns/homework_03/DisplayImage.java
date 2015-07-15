package com.example.arjuns.homework_03;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by arjuns on 7/5/2015.
 */
public class DisplayImage extends Activity {
    ImageView myDisplayImageView;
    Intent displayMediaIntent;
    String path;
    BitmapFactory.Options myBitmapOptions;
    String imageDate;
    TextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_image_layout);

        //Referencing the UI components present in display_image_layout.xml
        myDisplayImageView = (ImageView)findViewById(R.id.displayImageView);
        myTextView = (TextView)findViewById(R.id.textView2);

        displayMediaIntent = getIntent();
        path = displayMediaIntent.getStringExtra("filePath");

        //Setting the bitmap on imageView
        myBitmapOptions = new BitmapFactory.Options();
        myBitmapOptions.inSampleSize = 2;
        myDisplayImageView.setImageBitmap(BitmapFactory.decodeFile(path, myBitmapOptions));
    }
}
