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
import android.os.StrictMode;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    private Container container;


    boolean isRout = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
//vymaz();
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout,null);
        popupView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.abc_slide_in_bottom));
        popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);



        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



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



        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(myPosition.latitude, myPosition.longitude), 15));
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);




    }


    @Override
    public void onMapReady(GoogleMap map) {

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


        if (id == R.id.action_settings_settings) {

            startActivity(myIntent2(Settings.class));
         // navigationEnabled=!navigationEnabled;

            //test query na google maps
            //Uri gmmIntentUri = Uri.parse("geo:0,0?q=Evropska+Praha+6");
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
//            startActivity(mapIntent);



            return true;
        }
        if(id==R.id.action_settings_help){

            startActivity(myIntent2(Help.class));
            return true;
        }
        if(id==R.id.action_load){

            startActivity(myIntent2(Load.class));
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


                String[] routes = new String[4];
                try {
                    routes = readRouts();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] parsed = parseInput(routes);

                Route[] route1234 = new Route[4];
                for(int i=0;i<parsed.length;i++){
                    int k=0;
                    float[] routeDescription = new float[4];
                    for(int j=0;j<parsed[i].length();j++){
                        if(parsed[i].charAt(j)!=' '){
                            routeDescription[k]=(routeDescription[k]*10 +(parsed[i].charAt(j)-48));
                        }else{
                            k++;
                        }
                    }
                route1234[i]=new Route(routeDescription[0]/1000,routeDescription[1]/100,routeDescription[2]);

                }

                route1234[0].setPoints(parseRoutes(routes[0]));
                route1234[1].setPoints(parseRoutes(routes[1]));
                route1234[2].setPoints(parseRoutes(routes[2]));
                route1234[3].setPoints(parseRoutes(routes[3]));

                container= new Container(myPosition,destination,route1234[0],route1234[1],route1234[2],route1234[3]);



                startActivity(myIntent(RouteChooser.class));



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

    public String[] readRouts() throws IOException {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://its.felk.cvut.cz/cycle-planner-1.1.3-SNAPSHOT-junctions/bicycleJourneyPlanning/planJourneys?startLon="+myPosition.longitude+"&startLat="+myPosition.latitude+"&endLon="+destination.longitude+"&endLat="+destination.latitude+"&avgSpeed=20");

        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));

        String[] output = new String[4];
        //cameraBorder=new String[4];
        int number=0;
        String line;
        while ((line = reader.readLine()) != null) {
            if(line.length()>4){
                if(line.charAt(4)=='t'&&line.charAt(5)=='a'){

                    output[number]=builder.toString();
                    builder = new StringBuilder();
                    number++;
                }



            }


            builder.append(line);


        }
        return output;


    }
    public String[] parseInput(String[] input){
        String[] output= new String[4];
        for(int i = 0;i<input.length;i++){
            output[i]="";
            for(int j=0;j<input[i].length();j++){
                if(input[i].charAt(j)=='l'&&input[i].charAt(j+1)=='e'&&input[i].charAt(j+2)=='n'&&input[i].charAt(j+3)=='g'){
                    j=j+10;
                    while(input[i].charAt(j)!=','){
                        output[i]+=input[i].charAt(j);

                        j++;
                    }
                    output[i]+=" ";
                }

                if(input[i].charAt(j)=='d'&&input[i].charAt(j+1)=='u'&&input[i].charAt(j+2)=='r'&&input[i].charAt(j+3)=='a'){
                    j=j+12;
                    while(input[i].charAt(j)!=','){
                        output[i]+=input[i].charAt(j);

                        j++;
                    }
                    output[i]+=" ";
                }

                if(input[i].charAt(j)=='G'&&input[i].charAt(j+1)=='a'&&input[i].charAt(j+2)=='i'&&input[i].charAt(j+3)=='n'){
                    j=j+8;
                    while(input[i].charAt(j)!=','){
                        output[i]+=input[i].charAt(j);

                        j++;
                    }
                    output[i]+=" ";
                }

                if(input[i].charAt(j)=='D'&&input[i].charAt(j+1)=='r'&&input[i].charAt(j+2)=='o'&&input[i].charAt(j+3)=='p'){
                    j=j+8;
                    while(input[i].charAt(j)!=','){
                        output[i]+=input[i].charAt(j);

                        j++;
                    }
                    output[i]+=" ";
                }


            }

        }

        return output;
    }
public ArrayList<LatLng> parseRoutes(String route){
    ArrayList<String> latitudes = new ArrayList<String>();
    ArrayList<String> lontitudes = new ArrayList<String>();
    ArrayList<LatLng> output = new ArrayList<LatLng>();



    for (int i = 0; i < route.length(); i++) {
        if (route.charAt(i) == 'l' && route.charAt(i + 1) == 'e' && route.charAt(i + 2) == 'n') {
            break;
        }
        String latitude = "";
        String lontitude = "";
        if (route.charAt(i) == 'l' && route.charAt(i + 1) == 'a' && route.charAt(i + 2) == 't') {
            i = i + 8;
            for (int j = 0; j < 8; j++) {
                if (j == 2) {
                    latitude += ".";
                }
                i++;
                latitude += route.charAt(i);

            }
            latitudes.add(latitude);
        }
        if (route.charAt(i) == 'l' && route.charAt(i + 1) == 'o' && route.charAt(i + 2) == 'n') {
            i = i + 8;
            for (int j = 0; j < 8; j++) {
                if (j == 2) {
                    lontitude += ".";
                }
                i++;
                lontitude += route.charAt(i);

            }
            lontitudes.add(lontitude);
        }

    }
    PolylineOptions line = new PolylineOptions();
    for (int i = 0; i < latitudes.size(); i++) {
output.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(lontitudes.get(i))));

    }
    return output;
}

    public Intent myIntent(Class c){
        Intent intent;
        intent = new Intent(getApplicationContext(), c);
        intent.putExtra("boolean",false);
        intent.putExtra("intent",new Intent(this, NavigationActivity.class));
        intent.putExtra("coordinates2",container.getMyPosition());
        intent.putExtra("coordinates1",container.getDirection());

        for (int i = 0; i<4;i++){
            intent.putExtra("route" +i+"1", container.getRoutes()[i].getPoints());
            intent.putExtra("route" +i+"2", container.getRoutes()[i].getLength());
            intent.putExtra("route" +i+"3", container.getRoutes()[i].getDuration());
            intent.putExtra("route" +i+"4", container.getRoutes()[i].getAscent());

        }
        return intent;
    }
    public Intent myIntent2(Class c){
        Intent intent;
        intent = new Intent(getApplicationContext(), c);



        intent.putExtra("coordinates2",myPosition);
        intent.putExtra("intent",new Intent(this, NavigationActivity.class));
        intent.putExtra("boolean",true);


        return intent;
    }

    public void vymaz (){

        try {
            FileOutputStream fos = openFileOutput("saves", Context.MODE_PRIVATE);

            fos.write("".getBytes());

            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

