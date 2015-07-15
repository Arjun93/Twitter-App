package com.example.arjuns.homework_03;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by arjuns on 7/7/2015.
 */
public class TakePictureActivity extends Activity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.take_picture_layout);

        //invoke Camera2BasicFragment
        getFragmentManager().beginTransaction()
                .replace(R.id.cameraFragment
                        , Camera2BasicFragment.newInstance()).commit();

    }

}