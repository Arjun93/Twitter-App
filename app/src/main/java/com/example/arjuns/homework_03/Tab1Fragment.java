package com.example.arjuns.homework_03;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.sql.Date;

/**
 * Created by arjuns on 7/2/2015.
 */
public class Tab1Fragment extends Fragment {
    /**
     * Cursor used to access the results from querying for images on the SD card.
     */
    private Cursor cursor;
    /*
     * Column index for the Thumbnails Image IDs.
     */
    private int columnIndex;
    int i,numberOfFiles,fileNamesIndex,extensionsIndex;
    String[] filePaths,fileNames;
    GridView myGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.gallery_layout,container,false);
        myGridView = (GridView)myView.findViewById(R.id.tab1);

        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE};

        String selectionQuery = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        final String sortDescending = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        // Create the cursor pointing to the SDCard
        cursor = getActivity().managedQuery(MediaStore.Files.getContentUri("external"),
                projection, // Which columns to return
                selectionQuery,       // Return all rows
                null,
                sortDescending);

        numberOfFiles =cursor.getCount();
        filePaths = new String[numberOfFiles];
        fileNames = new String[numberOfFiles];
        final int[] fileNameExtensions = new int[numberOfFiles];
        final String[] fileDates = new String[numberOfFiles];

        // Get the column index of the Thumbnails Image ID
        for (i = 0; i < numberOfFiles; i++) {
            cursor.moveToPosition(i);
            columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            fileNamesIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
            extensionsIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int dateIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED);

            filePaths[i] = cursor.getString(columnIndex); // Get media file's path
            fileNames[i] = cursor.getString(fileNamesIndex); // Get media file's name
            fileNameExtensions[i] = cursor.getInt(extensionsIndex); // Get media file's name extension
            fileDates[i]= cursor.getString(dateIndex); // Get media file's date index
        }



        myGridView.setAdapter(new ImageAdapter(myView.getContext(), cursor, fileNameExtensions));

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA); // Get the data location of the media
                cursor.moveToPosition(position);
                String mediaPath = cursor.getString(columnIndex); // Use this path to do further processing, i.e. full screen display

                if(fileNameExtensions[position] == 3) {
                    // if the media type is a video, invoke DisplayVideo activity
                    Intent displayVideoIntent = new Intent(getActivity().getApplicationContext(), DisplayVideo.class);
                    displayVideoIntent.putExtra("filePath", mediaPath);
                    startActivity(displayVideoIntent);
                }
                else {
                    // if the media type an image, invoke DisplayImage activity
                    Intent displayMediaIntent = new Intent(getActivity().getApplicationContext(), DisplayImage.class);
                    displayMediaIntent.putExtra("filePath", mediaPath);
                    displayMediaIntent.putExtra("date", fileDates[position]);
                    startActivity(displayMediaIntent);
                }
            }
        });

        return myView;
    }

    /*Returns the cursor object*/
    public Cursor getCursor() {
        return cursor;
    }

    /*Returns the current column index*/
    public int getColumnIndex() {
        return columnIndex;
    }
}
