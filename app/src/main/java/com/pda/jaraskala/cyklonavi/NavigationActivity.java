package com.pda.jaraskala.cyklonavi;

import android.app.ActionBar;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NavigationActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean navigationEnabled=false;
    private Marker myMark=null;
    private Marker destinationMark=null;
    private EditText mapSearchBox;
    private LatLng destination;
    private LatLng myPosition;
    private PopupWindow popupWindow;
    private String route="";


    boolean isRout = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout,null);
        popupView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.abc_slide_in_bottom));
        popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);







        LocationManager manager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        myPosition=new LatLng(50.078455,14.400039);

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        BitmapDescriptor icon=BitmapDescriptorFactory.fromResource(R.drawable.marker);
        myMark=mMap.addMarker(new MarkerOptions().position(myPosition).title("position").icon(icon));





        LocationListener listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng position=new LatLng(location.getLatitude(),location.getLongitude());
                if(navigationEnabled) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(position));

                }
                myMark.setPosition(position);
                myPosition=position;

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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(myPosition.latitude, myPosition.longitude), 15));
    }

    protected void onPause(){
        super.onPause();


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

        Bundle extras = getIntent().getExtras();
        if(extras !=null){

            route= (String) extras.get("route");

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(myPosition.latitude, myPosition.longitude), 15));
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);


        /*mMap.addPolyline(new PolylineOptions().geodesic(true)
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
        );*/
        //parseRout();

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
       // itIsLast();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_settings) {
            Intent intent;
            intent = new Intent(this, Settings.class);
            intent.putExtra("intent",new Intent(this, NavigationActivity.class));
            startActivity(intent);
         // navigationEnabled=!navigationEnabled;

            //test query na google maps
            //Uri gmmIntentUri = Uri.parse("geo:0,0?q=Evropska+Praha+6");
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
//            startActivity(mapIntent);



            return true;
        }
        if(id==R.id.action_settings_help){
            Intent intent;
            intent = new Intent(this, Help.class);
            intent.putExtra("intent",new Intent(this, NavigationActivity.class));
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




    @Override
    public void onMapLongClick(LatLng latLng) {
        Button popButton=(Button)popupWindow.getContentView().findViewById(R.id.popButton);


        mMap.clear();


        destinationMark = mMap.addMarker(new MarkerOptions().position(latLng));
        BitmapDescriptor icon=BitmapDescriptorFactory.fromResource(R.drawable.marker);
        myMark=mMap.addMarker(new MarkerOptions().position(myPosition).title("position").icon(icon));
        destination=latLng;
        popupWindow.dismiss();
        popButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent;
                intent = new Intent(getApplicationContext(), RouteChooser.class);
                intent.putExtra("coordinates2",myPosition);
                intent.putExtra("coordinates1",destination);

                startActivity(intent);



            }
        });

        Geocoder gCoder = new Geocoder(this);

        try {
            List<Address> addresses = gCoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            TextView tv = (TextView) popupWindow.getContentView().findViewById(R.id.adress);
            if(addresses!= null && addresses.size()>0){

                tv.setText(addresses.get(0).getThoroughfare()+" "+addresses.get(0).getFeatureName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.BOTTOM,0,0);




    }


    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();

        BitmapDescriptor icon=BitmapDescriptorFactory.fromResource(R.drawable.marker);
        myMark=mMap.addMarker(new MarkerOptions().position(myPosition).title("position").icon(icon));
        popupWindow.dismiss();
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
                Geocoder geocoder = new Geocoder(getApplicationContext());
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


    public void parseRout(){
        System.out.println("PARSE");
        ArrayList<String> latitudes= new ArrayList<String>();
        ArrayList<String> lontitudes= new ArrayList<String>();
        if(route.equalsIgnoreCase("")){

        }else{


            isRout=true;

            for(int i=0;i<route.length();i++){
                if(route.charAt(i)=='l'&&route.charAt(i+1)=='e'&&route.charAt(i+2)=='n'){
                    break;
                }
                String latitude="";
                String lontitude="";
                if(route.charAt(i)=='l'&&route.charAt(i+1)=='a'&&route.charAt(i+2)=='t'){
                    i=i+8;
                    for (int j=0;j<8;j++){
                        if(j==2){
                            latitude+=".";
                        }
                        i++;
                        latitude+=route.charAt(i);

                    }
                    latitudes.add(latitude);
                }
                if(route.charAt(i)=='l'&&route.charAt(i+1)=='o'&&route.charAt(i+2)=='n'){
                    i=i+8;
                    for (int j=0;j<8;j++){
                        if(j==2){
                            lontitude+=".";
                        }
                        i++;
                        lontitude+=route.charAt(i);

                    }
                    lontitudes.add(lontitude);
                }

            }
            PolylineOptions line = new PolylineOptions();
            for(int i=0;i<latitudes.size();i++){

                line.geodesic(true).add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(lontitudes.get(i))));
                System.out.println(Double.parseDouble(latitudes.get(i))+" " +Double.parseDouble(lontitudes.get(i)));
            }
            line.color(Color.argb(255,102,0,204));
            //line.color(Color.argb(255,0,255,0));
            //line.color(Color.argb(255,255,0,0));
            //line.color(Color.argb(255,0,0,0));
            mMap.addPolyline(line);



        }


    }
}

