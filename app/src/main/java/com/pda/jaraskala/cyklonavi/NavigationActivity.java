package com.pda.jaraskala.cyklonavi;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NavigationActivity extends ActionBarActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean navigationEnabled=false;
    private Marker mark=null;
    private EditText mapSearchBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        String FILENAME = "cykloNaviSettings";
        String string = "";
        byte[] bytes =new byte[255];

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            string="1a2b3c";
            fos.write(string.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);

        LocationManager manager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        BitmapDescriptor icon=BitmapDescriptorFactory.fromResource(R.mipmap.pointer2);
        mark=mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("pozice").icon(icon));
        LocationListener listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng position=new LatLng(location.getLatitude(),location.getLongitude());
                if(navigationEnabled) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(position));

                }
                mark.setPosition(position);
                //mark=mMap.addMarker(new MarkerOptions().position(position).title("pozice").icon(icon));


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);

        //Search box

        mapSearchBox = (EditText) findViewById(R.id.search);
        mapSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);

                    new SearchClicked(mapSearchBox.getText().toString()).execute();
                    //mapSearchBox.setText("", TextView.BufferType.EDITABLE);
                    return true;
                }
                return false;
            }
        });


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
        BitmapDescriptor icon=BitmapDescriptorFactory.fromResource(R.drawable.kolo);
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("kolo").icon(icon));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(50.083872, 14.437499), 15));

        mMap.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(50.074, 14.448))
                .add(new LatLng(50.077, 14.448))
                .add(new LatLng(50.078, 14.448))
                .add(new LatLng(50.080, 14.447))
                .add(new LatLng(50.081, 14.445))
                .add(new LatLng(50.081, 14.444))
                .add(new LatLng(50.083, 14.443))
                .add(new LatLng(50.084399, 14.441216))
                .add(new LatLng(50.085036, 14.441114))
                .add(new LatLng(50.085136, 14.440709))
                .add(new LatLng(50.086174, 14.437802))
                .add(new LatLng(50.086489, 14.436973))
                .add(new LatLng(50.087264, 14.435220))
                .add(new LatLng(50.087269, 14.432607))
                .add(new LatLng(50.089678, 14.432412))
                .add(new LatLng(50.091074, 14.431888))
                .add(new LatLng(50.091528, 14.431781))
                .add(new LatLng(50.090704, 14.427769))
                .add(new LatLng(50.090677, 14.426057))
                .add(new LatLng(50.089510, 14.422672))
                .add(new LatLng(50.089290, 14.422796))
                .add(new LatLng(50.089331, 14.42379))
        );

    }


    @Override
    public void onMapReady(GoogleMap map) {
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(-18.142, 178.431), 2));
//
//        // Polylines are useful for marking paths and routes on the map.
//        map.addPolyline(new PolylineOptions().geodesic(true)
//                .add(new LatLng(-33.866, 151.195))  // Sydney
//                .add(new LatLng(-18.142, 178.431))  // Fiji
//                .add(new LatLng(21.291, -157.821))  // Hawaii
//                .add(new LatLng(37.423, -122.091))  // Mountain View
//        );
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
         // navigationEnabled=!navigationEnabled;

            //test query na google maps
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=Evropska+Praha+6");
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
//            startActivity(mapIntent);



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


    private class SearchClicked extends AsyncTask<Void, Void, Boolean> {
        private String toSearch;
        private Address address;

        public SearchClicked(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.UK);
                List<Address> results = geocoder.getFromLocationName(toSearch, 1);

                if (results.size() == 0) {
                    return false;
                }

                address = results.get(0);

                // cykloplanovac


            } catch (Exception e) {
                Log.e("", "Something went wrong: ", e);
                return false;
            }
            return true;
        }
    }


}

