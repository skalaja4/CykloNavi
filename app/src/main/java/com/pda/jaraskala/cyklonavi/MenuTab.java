package com.pda.jaraskala.cyklonavi;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TabHost;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MenuTab extends ActionBarActivity {
    private RadioButton rb;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private RadioButton rb5;
    private RadioButton rb6;
    private RadioButton rb7;

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

        rb = (RadioButton) findViewById(R.id.radioButton);
        rb.toggle();
        rb2 = (RadioButton) findViewById(R.id.radioButton2);
        rb3 = (RadioButton) findViewById(R.id.radioButton3);
        rb3.toggle();
        rb4 = (RadioButton) findViewById(R.id.radioButton4);
        rb5 = (RadioButton) findViewById(R.id.radioButton5);
        rb5.toggle();
        rb6 = (RadioButton) findViewById(R.id.radioButton6);
        rb7 = (RadioButton) findViewById(R.id.radioButton7);

        rb.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                reWriteSettings();
            }
        });
        rb2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                reWriteSettings();
            }
        });

        rb3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                reWriteSettings();
            }
        });
        rb4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                reWriteSettings();
            }
        });
        rb5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                reWriteSettings();
            }
        });
        rb6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                reWriteSettings();
            }
        });
        rb7.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                reWriteSettings();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        readFile();

    }

    @Override
    protected void onStop() {
        super.onStop();
        itIsLast();

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
           // openLastOne();
            NavUtils.navigateUpFromSameTask(this);
            return true;


        }

        return super.onOptionsItemSelected(item);
    }


    public void reWriteSettings(){

        String FILENAME = "cykloNaviSettings";
        String string = "";
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            string += "1"+rb.isChecked()+"2"+rb3.isChecked()+"3";
            if(rb5.isChecked()){
                string+="a";
            }
            if(rb6.isChecked()){
                string+="b";
            }if(rb7.isChecked()){
                string+="c";
            }
            fos.write(string.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    if(input.charAt(i+1)=='t'){
                        rb.toggle();
                    }else{
                        rb2.toggle();
                    }
                    continue;
                }
                if(input.charAt(i)=='2'){
                    if(input.charAt(i+1)=='t'){
                        rb3.toggle();
                    }else{
                        rb4.toggle();
                    }
                    continue;

                }

                if(input.charAt(i)=='3'){
                    if(input.charAt(i+1)=='a'){
                        rb5.toggle();
                    }else if(input.charAt(i+1)=='b'){
                        rb6.toggle();
                    }else{
                        rb7.toggle();
                    }
                    break;

                }

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    output+="4menu";
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
