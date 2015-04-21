package com.pda.jaraskala.cyklonavi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class TrackInfo extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);


    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView tv=  (TextView)findViewById(R.id.trackInfo);
        tv.setText(String.valueOf(System.currentTimeMillis()));

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {


        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_info, menu);
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
            Intent intent;
            intent = new Intent(this, menu.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_back){
            Intent intent;
            intent = new Intent(this, menu.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
