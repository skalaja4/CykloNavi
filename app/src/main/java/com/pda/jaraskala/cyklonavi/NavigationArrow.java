package com.pda.jaraskala.cyklonavi;

import android.content.DialogInterface;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

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

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_arrow);
        lastPassed=0;
        arrow =(ImageView) findViewById(R.id.imageView);

        arrow.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        points = (ArrayList<LatLng>)extras.get("points");

    mSensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



        LocationListener listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               // LatLng position=new LatLng(location.getLatitude(),location.getLongitude());
                if(navigationEnabled) {
                 //   mMap.animateCamera(CameraUpdateFactory.newLatLng(position));

                }

                myPosition=location;

                int closest = closestPoint();





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

       for(int i=lastPassed;i<points.size();i++){
            first = distance(myPosition,points.get(i));
           second = distance(myPosition,points.get(i+1));

           if(second>first){
               lastPassed=i;
               return i+1;
           }

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
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            arrow.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
    }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
