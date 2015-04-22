package com.pda.jaraskala.cyklonavi;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class CykloNavi extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyklo_navi);
        final Button button = (Button) findViewById(R.id.button_show_map);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNavigation();
            }
        });
        final Button button2 = (Button) findViewById(R.id.button_load_saves);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startLoadScreen();
            }
        });
        final ImageView imageView = (ImageView) findViewById(R.id.imageView2Main);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startMenu();
            }
        });
        startNavigation();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cyklo_navi, menu);
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
            startMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void startNavigation(){
        Intent intent;
        intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    public void startLoadScreen(){
        Intent intent;
        intent = new Intent(this, loadScreen.class);
        startActivity(intent);
    }
    public void startMenu(){
        Intent intent;
        intent = new Intent(this, MenuTab.class);
        startActivity(intent);
    }


//    public void buttonClicked(View v){
//        Button button = (Button) findViewById(R.id.button_show_map);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Do something in response to button click
//            }
//        });
//    }
}