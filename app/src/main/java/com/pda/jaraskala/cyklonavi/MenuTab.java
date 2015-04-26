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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MenuTab extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    String[] strings1 ={"English","Čeština"};
    String[] strings2 ={"km","mile"};
    String[] strings3 ={"Pointer 1","Pointer 2", "Pointer 3"};
    int arr_images[] ={R.mipmap.pointer1,R.mipmap.pointer2,R.mipmap.pointer3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_tab);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
         actionBar.setIcon(R.drawable.kolo);

        TabHost tabHost =(TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("settings");
        tabSpec.setContent(R.id.tab_settings);
        tabSpec.setIndicator("Settings");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("TrackInfo");
        tabSpec.setContent(R.id.tab_trackInfo);
        tabSpec.setIndicator("TrackInfo");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Saves");
        tabSpec.setContent(R.id.tab_saves);
        tabSpec.setIndicator("Saves");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Help");
        tabSpec.setContent(R.id.tab_help);
        tabSpec.setIndicator("Help");
        tabHost.addTab(tabSpec);



        Spinner mySpinner1 = (Spinner)findViewById(R.id.spinner);
        mySpinner1.setAdapter(new MyAdapter1(MenuTab.this, R.layout.row2, strings1));
        mySpinner1.setOnItemSelectedListener(this);

        Spinner mySpinner2 = (Spinner)findViewById(R.id.spinner2);
        mySpinner2.setAdapter(new MyAdapter2(MenuTab.this, R.layout.row2, strings2));
        mySpinner2.setOnItemSelectedListener(this);

        Spinner mySpinner3 = (Spinner)findViewById(R.id.spinner3);
        mySpinner3.setAdapter(new MyAdapter3(MenuTab.this, R.layout.row3, strings3));
        mySpinner3.setOnItemSelectedListener(this);


    }


    @Override
    protected void onStart() {
        super.onStart();
        readFile();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == android.R.id.home){

            NavUtils.navigateUpFromSameTask(this);
            return true;


        }

        return super.onOptionsItemSelected(item);
    }


    public String reWriteSettings(String object){

        String FILENAME = "cykloNaviSettings";
        String string = "";
        byte[] bytes =new byte[255];
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            FileInputStream fis = openFileInput(FILENAME);
            fis.read(bytes);
            String input = new String(bytes);

            if(object.toString()=="English"){
                for(int i=0; i<input.length();i++){
                    string+=input.charAt(i);
                    if(input.charAt(i)=='1'){
                    string+="a";
                        i++;
                    }
                }
            }
            if(object.toString()=="Čeština"){
                for(int i=0; i<input.length();i++){
                    string+=input.charAt(i);
                    if(input.charAt(i)=='1'){
                        string+="b";
                        i++;
                    }
                }
            }
            if(object.toString()=="km"){
                for(int i=0; i<input.length();i++){
                    string+=input.charAt(i);
                    if(input.charAt(i)=='2'){
                        string+="a";
                        i++;
                    }
                }
            }
            if(object.toString()=="mile"){
                for(int i=0; i<input.length();i++){
                    string+=input.charAt(i);
                    if(input.charAt(i)=='2'){
                        string+="b";
                        i++;
                    }
                }
            }
            if(object.toString()=="Pointer 1"){
                for(int i=0; i<input.length();i++){
                    string+=input.charAt(i);
                    if(input.charAt(i)=='3'){
                        string+="a";
                        i++;
                    }
                }
            }
            if(object.toString()=="Pointer 2"){
                for(int i=0; i<input.length();i++){
                    string+=input.charAt(i);
                    if(input.charAt(i)=='3'){
                        string+="b";
                        i++;
                    }
                }
            }
            if(object.toString()=="Pointer 3"){
                for(int i=0; i<input.length();i++){
                    string+=input.charAt(i);
                    if(input.charAt(i)=='3'){
                        string+="c";
                        i++;
                    }
                }
            }
            fos.write(string.getBytes());
            fos.close();
            System.out.println(string);
            if(string==""){
                return "Taky nic";
            }
            return string;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "NIC";
    }

    public void readFile(){


        String FILENAME = "cykloNaviSettings";

        byte[] bytes =new byte[255];
        try {
            FileInputStream fos = openFileInput(FILENAME);
            fos.read(bytes);
            String input = new String(bytes);
            for(int i=0; i<input.length();i++){
                if(input.charAt(i)=='1'){


                }

            }


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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //TextView tv = (TextView) findViewById(R.id.pokus);

        //tv.setText(reWriteSettings(parent.getItemAtPosition(position).toString()));
      //  tv.append(parent.getItemAtPosition(position).toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public class MyAdapter1 extends ArrayAdapter<String>{

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
