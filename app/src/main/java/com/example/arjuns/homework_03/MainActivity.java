package com.example.arjuns.homework_03;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        ActionBar myActionBar = getActionBar(); //Creating an ActionBar
        myActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab1 = myActionBar.newTab(); //Creating first tab for the Actionbar
        tab1.setText("Universal Player");
        TabListener<Tab1Fragment> t1 = new TabListener<Tab1Fragment>(this, "Universal Player", Tab1Fragment.class);
        tab1.setTabListener(t1); //Adding listener to the first tab
        myActionBar.addTab(tab1); //Adding first tab to the Actionbar

        ActionBar.Tab tab2 = myActionBar.newTab(); //Creating second tab for the Actionbar
        tab2.setText("Take Photo/ Video");
        TabListener<Tab2Fragment> t2 = new TabListener<Tab2Fragment>(this, "Take Photo/ Video", Tab2Fragment.class);
        tab2.setTabListener(t2); //Adding listener to the second tab
        myActionBar.addTab(tab2); //Adding first tab to the Actionbar
    }

    private class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment myFragment;
        private final Activity myActivity;
        private final Class<T> myClass;
        private final String myTag;

        public TabListener(Activity activity, String tag, Class<T> clzz) {
            myActivity = activity;
            myTag = tag;
            myClass = clzz;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if(myFragment == null) {
                myFragment = Fragment.instantiate(myActivity,myClass.getName());
                ft.replace(R.id.maintab,myFragment);
            }
            else {
                ft.attach(myFragment);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if(myFragment != null) {
                ft.detach(myFragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
