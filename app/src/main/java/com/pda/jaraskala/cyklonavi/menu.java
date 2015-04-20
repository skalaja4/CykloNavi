package com.pda.jaraskala.cyklonavi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class menu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startNavigationActivity();
                    }
                });
            }
        });

        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startLoadScreen();
            }
        });

        final Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startTrackInfo();

            }
        });

        final Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startSettings();
            }
        });

        final Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TextView tv = (TextView) findViewById(R.id.textViewHelp);
                tv.setText(readFile());
                startHelp();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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

    public void startLoadScreen(){
        Intent intent;
        intent = new Intent(this, loadScreen.class);
        startActivity(intent);
    }
    public void startSettings(){
        Intent intent;
        intent = new Intent(this, settings.class);
        startActivity(intent);
    }
    public void startHelp(){
        Intent intent;
        intent = new Intent(this, Help.class);
        startActivity(intent);
    }
    public void startTrackInfo(){
        Intent intent;
        intent = new Intent(this, TrackInfo.class);
        startActivity(intent);
    }
    public void startNavigationActivity(){
        Intent intent;
        intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }
    public String readFile(){
        String FILENAME = "cykloNaviSettings";
        String string = "";
        byte[] bytes =new byte[255];
        try {
            FileInputStream fos = openFileInput(FILENAME);
            fos.read(bytes);
            String input = new String(bytes);
            return input;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Nepovedlo se";

    }


}
