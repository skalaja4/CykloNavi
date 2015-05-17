package com.pda.jaraskala.cyklonavi;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class TrackInfo extends ActionBarActivity {

    LatLng direction;
    LatLng myPosition2;
    Container container;
    Route[] routes = new Route[4];
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();

        if(extras !=null) {
            if(!(boolean)extras.get("boolean")){
                position=(int) extras.get("position");
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
                myPosition2 = container.getMyPosition();

            }
        }

        TextView tvLength = (TextView) findViewById(R.id.trackInfo_length);
        TextView tvDuration = (TextView) findViewById(R.id.trackInfo_duration);
        TextView tvAscent = (TextView) findViewById(R.id.trackInfo_ascent);


        tvLength.setText("Length:           "+container.getRoutes()[position].length+" km");
        tvDuration.setText("Time:              "+container.getRoutes()[position].duration+" min");
        tvAscent.setText("Total ascent: "+container.getRoutes()[position].ascent+" m");


        GraphView graph = (GraphView) findViewById(R.id.graph);


        ArrayList<Integer> profile = parseProfile();
        ArrayList<Integer> second = new ArrayList<>();
        ArrayList<Integer> first = new ArrayList<>();

        int k=0;
        boolean isSecond=false;
        for(Integer i:profile){
            if(!isSecond){

                if(i==-9999){
                    isSecond=true;
                }else{
                first.add(i);}
            }else{
                second.add(i);
            }

        }



        k=0;
        DataPoint[] neco = new DataPoint[first.size()];

        for(Integer i:first){

                neco[k] = new DataPoint(Math.floor((((double)second.get(k))/1000)*100)/100, first.get(k));
            k++;

        }



        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(neco);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(Math.floor((((double)second.get(second.size()-1))/1000)*100)/100);
        graph.getViewport().setMinX(0);

        graph.addSeries(series);




    }
    public ArrayList<Integer> parseProfile(){
        ArrayList<Integer> output = new ArrayList<Integer>();

        for(int i=0;i<routes[position].string.length();i++){
            char c=routes[position].string.charAt(i);
            if(routes[position].string.charAt(i)=='P'&&routes[position].string.charAt(i+1)=='r'){

                int tmp=0;
                for(int j=i+13;i<routes[position].string.length();j++) {
                    c=routes[position].string.charAt(j);
                    if(routes[position].string.charAt(j)==' '&&routes[position].string.charAt(j+1)==']'){
                        i=j;
                        output.add(-9999);
                        break;
                    }
                    if(c==','){
                        j++;
                        output.add(tmp);
                        tmp =0;

                    }else{
                        tmp=(tmp*10)+(c-48);
                    }

                }

            }

            if(routes[position].string.charAt(i)=='D'&&routes[position].string.charAt(i+1)=='i'){

                int tmp=0;
                for(int j=i+14;i<routes[position].string.length();j++) {
                    c=routes[position].string.charAt(j);
                    if(routes[position].string.charAt(j)==' '&&routes[position].string.charAt(j+1)==']'){
                        i=j;
                        break;
                    }
                    if(c==','){
                        j++;
                        output.add(tmp);
                        tmp =0;

                    }else{
                        tmp=(tmp*10)+(c-48);
                    }

                }
                break;
            }

        }


        return output;

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
        if (id == android.R.id.home) {

            ArrayList<LatLng> points = routes[position].points;

            Intent intent;
            intent = new Intent(getApplicationContext(), NavigationArrow.class);
            intent.putExtra("points",points);
            intent.putExtra("boolean",false);
            intent.putExtra("coordinates2",container.getMyPosition());
            intent.putExtra("coordinates1",container.getDirection());
            intent.putExtra("position",position);
            intent.putExtra("intent",new Intent(this, RouteChooser.class));
            for (int i = 0; i<4;i++){
                intent.putExtra("route" +i+"1", container.getRoutes()[i].getPoints());
                intent.putExtra("route" +i+"2", container.getRoutes()[i].getLength());
                intent.putExtra("route" +i+"3", container.getRoutes()[i].getDuration());
                intent.putExtra("route" +i+"4", container.getRoutes()[i].getAscent());
                intent.putExtra("route" +i+"5", container.getRoutes()[i].getString());

            }

            NavUtils.navigateUpTo(this, intent);





            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
