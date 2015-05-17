package com.pda.jaraskala.cyklonavi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class Setting extends ActionBarActivity implements  AdapterView.OnItemSelectedListener {

    Spinner mySpinner1;
    Spinner mySpinner2;
    Spinner mySpinner3;
    String[] strings1 ={"English","Čeština"};
    String[] strings2 ={"km","mile"};
    String[] strings3 ={"Pointer 1","Pointer 2", "Pointer 3"};
    int arr_images[] ={R.drawable.marker,R.drawable.marker1,R.drawable.marker2};
    LatLng direction;
    LatLng myPosition;
    Container container;
    Route[] routes = new Route[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mySpinner1 = (Spinner)findViewById(R.id.spinner);
        mySpinner1.setAdapter(new MyAdapter1(Setting.this, R.layout.row2, strings1));
        mySpinner1.setOnItemSelectedListener(this);


        mySpinner2 = (Spinner)findViewById(R.id.spinner2);
        mySpinner2.setAdapter(new MyAdapter2(Setting.this, R.layout.row2, strings2));
        mySpinner2.setOnItemSelectedListener(this);

        mySpinner3 = (Spinner)findViewById(R.id.spinner3);
        mySpinner3.setAdapter(new MyAdapter3(Setting.this, R.layout.row3, strings3));
        mySpinner3.setOnItemSelectedListener(this);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            if(!(boolean)extras.get("boolean")){
            for (int i = 0; i < 4; i++) {
                routes[i] = new Route();
                routes[i].points = (ArrayList<LatLng>) extras.get("route" + i + "1");
                routes[i].length = (float) extras.get("route" + i + "2");
                routes[i].duration = (float) extras.get("route" + i + "3");
                routes[i].ascent = (float) extras.get("route" + i + "4");
                routes[i].string=(String)extras.get("route" +i+"5");
            }
            container = new Container((LatLng) extras.get("coordinates2"), (LatLng) extras.get("coordinates1"), routes[0], routes[1], routes[2], routes[3]);
            direction = container.getDirection();
            myPosition = container.getMyPosition();
        }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

        if(id == android.R.id.home){

            Bundle extras = getIntent().getExtras();
            if(extras !=null){
                if(!(boolean)extras.get("boolean")) {
                    Intent intent = (Intent) extras.get("intent");
                    intent.putExtra("coordinates2", container.getMyPosition());
                    intent.putExtra("coordinates1", container.getDirection());

                    for (int i = 0; i < 4; i++) {
                        intent.putExtra("route" + i + "1", container.getRoutes()[i].getPoints());
                        intent.putExtra("route" + i + "2", container.getRoutes()[i].getLength());
                        intent.putExtra("route" + i + "3", container.getRoutes()[i].getDuration());
                        intent.putExtra("route" + i + "4", container.getRoutes()[i].getAscent());
                        intent.putExtra("route" +i+"5", container.getRoutes()[i].getString());

                    }

                    NavUtils.navigateUpTo(this, intent);

                }else{
                    Intent intent = (Intent) extras.get("intent");
                    NavUtils.navigateUpTo(this, intent);
                }
            }
           // NavUtils.navigateUpFromSameTask(this);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public class MyAdapter1 extends ArrayAdapter<String> {

        public MyAdapter1(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row2, parent, false);
            TextView label=(TextView)row.findViewById(R.id.company2);
            label.setText(strings1[position]);



            return row;
        }

    }
    public class MyAdapter2 extends ArrayAdapter<String>{

        public MyAdapter2(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row2, parent, false);
            TextView label=(TextView)row.findViewById(R.id.company2);
            label.setText(strings2[position]);



            return row;
        }
    }
    public class MyAdapter3 extends ArrayAdapter<String>{

        public MyAdapter3(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row3, parent, false);
            TextView label=(TextView)row.findViewById(R.id.company);
            label.setText(strings3[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.image);
            icon.setImageResource(arr_images[position]);

            return row;
        }
    }


}
