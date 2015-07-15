package com.example.arjuns.homework_03;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by arjuns on 7/2/2015.
 */
public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private Cursor myMediaCursor;
    Tab1Fragment myFetcher;
    private static LayoutInflater myInflater;
    ImageView myGridImageView;
    TextView myGridElementTextView;
    int[] myfileNameExtension;

    // Constructor
    public ImageAdapter(Context c, Cursor cursor, int[] fileNameExtension){
        mContext = c;
        myMediaCursor = cursor;
        myInflater =  (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myfileNameExtension=fileNameExtension;
    }

    @Override
    public int getCount() {
        return myMediaCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        BitmapFactory.Options myBitmapOptions = new BitmapFactory.Options();
        myBitmapOptions.inSampleSize = 8;
        if (view == null) {
            view = myInflater.inflate(R.layout.grid_view_element_layout, null);
        }
        myGridImageView = (ImageView)view.findViewById(R.id.myGridImageView);
        myGridElementTextView = (TextView)view.findViewById(R.id.textView2);
        // Move cursor to current position
        myMediaCursor.moveToPosition(position);
        // Get the current value for the requested column
        int myColumnIndex = myMediaCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        String mediaPath = myMediaCursor.getString(myColumnIndex);
        File myFile = new File(mediaPath);
        Log.i("MediaPath",""+mediaPath);
        if(myFile.exists())
        {
           if(myfileNameExtension[position] == 3 ){
               //if media is a video
               myGridElementTextView.setText("Video");
               Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mediaPath, MediaStore.Video.Thumbnails.MICRO_KIND);
               myGridImageView.setImageBitmap(bitmap);
           }
            else {
               //if media is an image
               myGridElementTextView.setText("Image");
               myGridImageView.setImageBitmap(BitmapFactory.decodeFile(mediaPath, myBitmapOptions));
           }
        }

        return view;
    }

}
