package com.pda.jaraskala.cyklonavi;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class NavigationArrow extends ActionBarActivity implements View.OnClickListener,SensorEventListener {
ImageView arrow;
float angle=0;
ArrayList<LatLng> points;
    int lastPassed;
    Location myPosition;
    private boolean navigationEnabled=false;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private SensorEventListener c;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private float difference=0;
    private GoogleMap mMap;
    PolylineOptions secondLine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_arrow);
        lastPassed=0;
        arrow =(ImageView) findViewById(R.id.imageView);
c=this;
        arrow.setOnClickListener(this);
        secondLine = new PolylineOptions();
        //secondLine.color(Color.argb(255, 0, 255, 0));
        Bundle extras = getIntent().getExtras();
        points = (ArrayList<LatLng>)extras.get("points");
        myPosition=new Location("");
        myPosition.setLatitude(50.009616);
        myPosition.setLongitude(14.633976);


        //myPosition.setLatitude(50.078455);
        //myPosition.setLongitude(14.400039);




    mSensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        LocationManager manager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);




      /*  Location loc2 = new Location("");
        loc2.setLatitude(points.get(2).latitude);
        loc2.setLongitude(points.get(2).longitude);
        Location loc = new Location("");
        loc.setLatitude(points.get(0).latitude);
        loc.setLongitude(points.get(0).longitude);

        difference = loc.bearingTo(loc2);
        System.out.println(difference);*/


        LocationListener listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               // LatLng position=new LatLng(location.getLatitude(),location.getLongitude());
                if(navigationEnabled) {
                 //   mMap.animateCamera(CameraUpdateFactory.newLatLng(position));

                }
            if(location!=null){
                myPosition=location;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myPosition.getLatitude(), myPosition.getLongitude()), 19));
                int closest = closestPoint();
                if(closest!=-1){
                    if(closest!=-2) {
                        Location loc2 = new Location("");
                        loc2.setLatitude(points.get(closest).latitude);
                        loc2.setLongitude(points.get(closest).longitude);
                        difference = myPosition.bearingTo(loc2);
                        System.out.println(difference);
                      //  secondLine.geodesic(true).add(points.get(closest - 2));
                        mMap.clear();
                        secondLine = new PolylineOptions();
                        for(int i =lastPassed;i<points.size();i++){
                            secondLine.geodesic(true).add(points.get(i));
                        }
                        mMap.addPolyline(secondLine);

                    }else{
                        arrow.setImageResource(R.drawable.finish);
                        mSensorManager.unregisterListener(c, mAccelerometer);
                        mSensorManager.unregisterListener(c, mMagnetometer);
                    }
                }
            }







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
        //myPosition=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_arrow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
       /* Matrix matrix = new Matrix();
        arrow.setScaleType(ImageView.ScaleType.MATRIX);
        //,arrow.getDrawable().getBounds().width()/2,arrow.getDrawable().getBounds().height()/2
        matrix.postRotate(60);
        arrow.setImageMatrix(matrix);

        angle+=60;
        if(angle==360){
            angle=0;
        }
        arrow.setRotation(angle);*/

    }

   public int closestPoint(){

       double first =0;
       double second = 0;
if(lastPassed+2<points.size()){
       for(int i=lastPassed;i<points.size()-1;i++){
            first = distance(myPosition,points.get(i));
           second = distance(myPosition,points.get(i+1));

           if(second>first){
               lastPassed=i;
               if(i+2<points.size()){
               return i+2;
               }else if(i+1<points.size()){
                   return i+1;

               }else{
                   return -2;
               }
           }

       }
}else{
    return -2;
}

return -1;

    }
    public double distance(Location position, LatLng point){
        Location loc = new Location("");
        loc.setLatitude(point.latitude);
        loc.setLongitude(point.longitude);
        return position.distanceTo(loc);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

       if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
            //System.out.println("TRUE1");
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            //System.out.println("TRUE2");
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;

        //float azimuthInDegress = Math.round(event.values[0]);


            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree+difference,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            System.out.println("ZMENA ROTACE");
            ra.setFillAfter(true);

            arrow.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
    }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map3))
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

        System.out.println("Pozice:" +myPosition.getLatitude());
        System.out.println("Pozice2:" +myPosition.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myPosition.getLatitude(), myPosition.getLongitude()), 19));



       // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(rohy(),500,500,0));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(rohy(),100,100,5));


      //  int[] colors = {Color.argb(255, 102, 0, 204), Color.argb(255, 0, 255, 0),Color.argb(255,255,0,0),Color.argb(255, 0, 0, 0)};

            PolylineOptions line = new PolylineOptions();
            for (int i = 0; i < points.size(); i++) {

                line.geodesic(true).add(points.get(i));


        }
        mMap.addPolyline(line);
    }

}
