package com.example.arjuns.homework_03;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arjuns on 7/2/2015.
 */
public class Tab2Fragment extends Fragment {
    private static View view;
    private static final int REQUEST_CODE_IMAGE = 3;
    private static final int REQUEST_CODE_VIDEO = 4;
    ImageView myPhotoView;
    VideoView myVideoView;
    BitmapFactory.Options myBitmapOptions;
    Button myCaptureImageButton, myCaptureVideoButton, mySaveButton, myDiscardButton, myShareButton;
    File myMediaFile;
    String filePath1,filePath2;
    boolean isImageFlag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.camera_layout, container, false);

        //Referencing the UI components present in camera_layout.xml
        myCaptureImageButton = (Button) view.findViewById(R.id.capturePhotoButton);
        myCaptureVideoButton = (Button) view.findViewById(R.id.captureVideoButton);
        myShareButton = (Button) view.findViewById(R.id.share_button);
        mySaveButton = (Button) view.findViewById(R.id.saveButton);
        myDiscardButton = (Button) view.findViewById(R.id.discardButton);
        myPhotoView = (ImageView) view.findViewById(R.id.photoView);
        myVideoView = (VideoView) view.findViewById(R.id.videoView);

        //Listener for capturing image
        myCaptureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent invokeCameraIntent1 = new Intent(getActivity().getApplicationContext(), TakePictureActivity.class);
                startActivityForResult(invokeCameraIntent1, REQUEST_CODE_IMAGE);
            }
        });

        //Listener for capturing video
        myCaptureVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent invokeCameraIntent2 = new Intent(getActivity().getApplicationContext(), TakeVideoActivity.class);
                startActivityForResult(invokeCameraIntent2, REQUEST_CODE_VIDEO);
            }
        });

        //Listener for discarding the media
        myDiscardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaFile.delete();
                Toast.makeText(getActivity().getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                myVideoView.setVisibility(View.INVISIBLE);
                myPhotoView.setVisibility(View.INVISIBLE);
            }
        });

        //Listener for saving the media
        mySaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String currentTime = myDateFormat.format(new Date());
                if(isImageFlag) {
                    //if the media is an image
                    String fileName = "IMAGE_" + currentTime + ".jpg";
                    myMediaFile = new File(filePath1, fileName);

                    //Setting the content values for the image to be displayed in the gallery
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Files.FileColumns.DATE_ADDED, currentTime);
                    values.put(MediaStore.Files.FileColumns.DATE_MODIFIED, currentTime);
                    values.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    values.put(MediaStore.Files.FileColumns.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Files.FileColumns.DATA, filePath1);
                    getActivity().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                    Toast.makeText(getActivity().getApplicationContext(), "Location: " + filePath1, Toast.LENGTH_SHORT).show();

                    //Hiding the buttons after save or discard.
                    mySaveButton.setVisibility(View.INVISIBLE);
                    myDiscardButton.setVisibility(View.INVISIBLE);
                }
                else {
                    //if the media is a video
                    String fileName = "VIDEO_" + currentTime + ".mp4";
                    myMediaFile = new File(filePath2, fileName);

                    //Setting the content values for the videos to be displayed in the gallery
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Files.FileColumns.DATE_ADDED, currentTime);
                    values.put(MediaStore.Files.FileColumns.DATE_MODIFIED, currentTime);
                    values.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                    values.put(MediaStore.Files.FileColumns.MIME_TYPE, "video/mp4");
                    values.put(MediaStore.Files.FileColumns.DATA, filePath2);
                    getActivity().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                    Toast.makeText(getActivity().getApplicationContext(), "Location: " + filePath2, Toast.LENGTH_SHORT).show();

                    //Hiding the buttons after save or discard.
                    mySaveButton.setVisibility(View.INVISIBLE);
                    myDiscardButton.setVisibility(View.INVISIBLE);
                }

                showLog(); //Print the contents to the logcat.

                myVideoView.setVisibility(View.INVISIBLE); //Setting visibility back to invisible
                myPhotoView.setVisibility(View.INVISIBLE); //Setting visibility back to invisible
            }
        });

        myShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFlag) {
                    Intent tweetIntent = new Intent(getActivity().getApplicationContext(),TweetActivity.class);
                    startActivity(tweetIntent);
                }
            }
        });

        return view;
    }

    /*showLog() prints the andrew id, device name, os version and current date onto the logcat*/
    public void showLog() {
        String andrewID = this.getActivity().getApplicationContext().getString(R.string.andrewID);
        String deviceName = Build.BRAND + " " + Build.DEVICE;
        String osVersion = Build.VERSION.RELEASE;
        SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date myDate = new Date();
        String currentDateTime = myDateFormat.format(myDate);
        Log.i("INFORMATION", "" + andrewID + " : " + deviceName + " " + osVersion + " : " + currentDateTime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        myPhotoView.setVisibility(View.GONE);
        myVideoView.setVisibility(View.GONE);
        mySaveButton.setVisibility(View.GONE);
        myDiscardButton.setVisibility(View.GONE);
        myShareButton.setVisibility(View.GONE);
        filePath1= "";
        filePath2= "";
        switch (requestCode) {
            case (REQUEST_CODE_IMAGE):
                if (resultCode == Activity.RESULT_OK) {
                    isImageFlag = true;
                    filePath1 = data.getStringExtra("imageFilePath");
                    myVideoView.setVisibility(View.INVISIBLE);
                    myPhotoView.setVisibility(View.VISIBLE);
                    myShareButton.setVisibility(View.VISIBLE);
                    myBitmapOptions = new BitmapFactory.Options();
                    myBitmapOptions.inSampleSize = 2;
                    Bitmap myBitmap = BitmapFactory.decodeFile(filePath1,myBitmapOptions);
                    myPhotoView.setImageBitmap(myBitmap); //Setting the image on image preview
                    myMediaFile = new File(filePath1);
                    if(myMediaFile!=null) {
                        mySaveButton.setVisibility(View.VISIBLE);
                        myDiscardButton.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case (REQUEST_CODE_VIDEO):
                if (resultCode == Activity.RESULT_OK) {
                    isImageFlag = false;
                    filePath2 = data.getStringExtra("videoFilePath");
                    myPhotoView.setVisibility(View.INVISIBLE);
                    myVideoView.setVisibility(View.VISIBLE);
                    myShareButton.setVisibility(View.VISIBLE);
                    Log.i("FILEPATH", "" + filePath2);
                    myVideoView.setVideoPath(filePath2); //setting the video on video preview
                    myVideoView.requestFocus();
                    myVideoView.start();
                    myMediaFile = new File(filePath2);
                    if(myMediaFile!=null) {
                        mySaveButton.setVisibility(View.VISIBLE);
                        myDiscardButton.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }
}
