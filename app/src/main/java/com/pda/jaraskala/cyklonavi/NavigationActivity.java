package com.pda.jaraskala.cyklonavi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NavigationActivity extends ActionBarActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-18.142, 178.431), 2));

        // Polylines are useful for marking paths and routes on the map.
        map.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(-33.866, 151.195))  // Sydney
                .add(new LatLng(-18.142, 178.431))  // Fiji
                .add(new LatLng(21.291, -157.821))  // Hawaii
                .add(new LatLng(37.423, -122.091))  // Mountain View
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        itIsLast();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent;
            intent = new Intent(this, MenuTab.class);
            startActivity(intent);
            return true;
        }






        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }
    public void itIsLast(){
        String FILENAME = "cykloNaviSettings";

        byte[] bytes =new byte[255];
        try {
            FileInputStream fis = openFileInput(FILENAME);
            fis.read(bytes);
            String input = new String(bytes);
            String output="";
            for(int i=1; i<input.length();i++){
                output+=input.charAt(i);

                if(input.charAt(i-1)=='3'){
                    output+="4google";
                    break;
                }


            }

            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(output.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void openLastOne()  {
        String FILENAME = "cykloNaviSettings";

        byte[] bytes =new byte[255];
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fis.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String input = new String(bytes);

        for(int i=0; i<input.length();i++){

            if(input.charAt(i)=='4'){

                if(input.charAt(i+1)=='m'){
                    Intent intent;
                    intent = new Intent(this, MenuTab.class);
                    startActivity(intent);
                    break;
                }

                if(input.charAt(i+1)=='g'){
                    Intent intent;
                    intent = new Intent(this, NavigationActivity.class);
                    startActivity(intent);
                    break;
                }

                if(input.charAt(i+1)=='n'){

                    break;
                }



            }


        }

    }
}
