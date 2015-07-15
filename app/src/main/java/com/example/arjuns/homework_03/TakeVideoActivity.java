package com.example.arjuns.homework_03;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by arjuns on 7/7/2015.
 */
public class TakeVideoActivity extends Activity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.take_video_layout);

        //invoke Camera2VideoFragment
        getFragmentManager().beginTransaction()
                .replace(R.id.videoFragment
                        , Camera2VideoFragment.newInstance()).commit();

    }
}
