package com.pda.jaraskala.cyklonavi;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class Help extends ActionBarActivity {

    LatLng direction;
    LatLng myPosition;
    Container container;
    Route[] routes = new Route[4];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if(!(boolean)extras.get("boolean")) {
        for(int i=0;i<4;i++){
            routes[i]=new Route();
            routes[i].points=(ArrayList<LatLng>)extras.get("route" +i+"1");
            routes[i].length=(float)extras.get("route" +i+"2");
            routes[i].duration=(float)extras.get("route" +i+"3");
            routes[i].ascent=(float)extras.get("route" +i+"4");
            routes[i].string=(String)extras.get("route" +i+"5");
        }
        container = new Container((LatLng)extras.get("coordinates2"),(LatLng)extras.get("coordinates1"),routes[0],routes[1],routes[2],routes[3]) ;
        direction=container.getDirection();
        myPosition=container.getMyPosition();
    }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
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
                intent.putExtra("coordinates2",container.getMyPosition());
                intent.putExtra("coordinates1",container.getDirection());

                for (int i = 0; i<4;i++){
                    intent.putExtra("route" +i+"1", container.getRoutes()[i].getPoints());
                    intent.putExtra("route" +i+"2", container.getRoutes()[i].getLength());
                    intent.putExtra("route" +i+"3", container.getRoutes()[i].getDuration());
                    intent.putExtra("route" +i+"4", container.getRoutes()[i].getAscent());
                    intent.putExtra("route" +i+"5", container.getRoutes()[i].getString());

                }

                NavUtils.navigateUpTo(this, intent);


            }else{
                    Intent intent = (Intent) extras.get("intent");
                    NavUtils.navigateUpTo(this, intent);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
