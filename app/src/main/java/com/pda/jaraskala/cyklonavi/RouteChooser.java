package com.pda.jaraskala.cyklonavi;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class RouteChooser extends ActionBarActivity implements AdapterView.OnItemClickListener {
    LatLng direction;
    LatLng myPostition;
    ListView listView;
    String[] routes;
    private LatLng destination;
    private LatLng myPosition;
    private GoogleMap mMap;
    private Marker myMark=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_chooser);
        listView=(ListView)findViewById(R.id.listViewRoute);



        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            direction = (LatLng) extras.get("coordinates1");
            myPostition = (LatLng) extras.get("coordinates2");
        }
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        myPosition=new LatLng(50.078455,14.400039);





        String[] input = new String[4];
        try {
            input = readRouts();
            routes=input;
            String parsed[] = parseInput(input);




            ArrayList<SingleRow> arrayList = new ArrayList<SingleRow>();


            Resources res = getResources();
            String[] titles = res.getStringArray(R.array.typeRouteTitles);
            for(int i=0;i<parsed.length;i++){
                int k=0;
                float[] routeDescription = new float[4];
                for(int j=0;j<parsed[i].length();j++){
                    if(parsed[i].charAt(j)!=' '){
                    routeDescription[k]=(routeDescription[k]*10 +(parsed[i].charAt(j)-48));
                    }else{
                    k++;
                    }
                }
                arrayList.add(new SingleRow(titles[i],routeDescription[0]/1000+" km, "+routeDescription[1]/100 +" min, ascent: "+routeDescription[2]+" m"));
            }




            listView.setAdapter(new MyAdapter(arrayList,this));
            listView.setOnItemClickListener(this);



        } catch (IOException e) {
            e.printStackTrace();
        }



        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        BitmapDescriptor icon= BitmapDescriptorFactory.fromResource(R.drawable.marker);
        myMark=mMap.addMarker(new MarkerOptions().position(myPosition).title("position").icon(icon));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_route_chooser, menu);
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
            intent = new Intent(this, MenuTab.class);
            startActivity(intent);
        return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String[] readRouts() throws IOException {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://its.felk.cvut.cz/cycle-planner-1.1.3-SNAPSHOT-junctions/bicycleJourneyPlanning/planJourneys?startLon="+myPostition.longitude+"&startLat="+myPostition.latitude+"&endLon="+direction.longitude+"&endLat="+direction.latitude+"&avgSpeed=20");

            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            String[] output = new String[4];
            int number=0;
            String line;
            while ((line = reader.readLine()) != null) {
            if(line.length()>4){
                if(line.charAt(4)=='t'&&line.charAt(5)=='a'){

                    output[number]=builder.toString();
                    builder = new StringBuilder();
                    number++;
                }
            }


                builder.append(line);


            }
        return output;


    }

    public String[] parseInput(String[] input){
        String[] output= new String[4];
        for(int i = 0;i<input.length;i++){
            output[i]="";
            for(int j=0;j<input[i].length();j++){
                if(input[i].charAt(j)=='l'&&input[i].charAt(j+1)=='e'&&input[i].charAt(j+2)=='n'&&input[i].charAt(j+3)=='g'){
                j=j+10;
                    while(input[i].charAt(j)!=','){
                        output[i]+=input[i].charAt(j);

                        j++;
                    }
                    output[i]+=" ";
                }

                if(input[i].charAt(j)=='d'&&input[i].charAt(j+1)=='u'&&input[i].charAt(j+2)=='r'&&input[i].charAt(j+3)=='a'){
                    j=j+12;
                    while(input[i].charAt(j)!=','){
                        output[i]+=input[i].charAt(j);

                        j++;
                    }
                    output[i]+=" ";
                }

                if(input[i].charAt(j)=='G'&&input[i].charAt(j+1)=='a'&&input[i].charAt(j+2)=='i'&&input[i].charAt(j+3)=='n'){
                    j=j+8;
                    while(input[i].charAt(j)!=','){
                        output[i]+=input[i].charAt(j);

                        j++;
                    }
                    output[i]+=" ";
                }

                if(input[i].charAt(j)=='D'&&input[i].charAt(j+1)=='r'&&input[i].charAt(j+2)=='o'&&input[i].charAt(j+3)=='p'){
                    j=j+8;
                    while(input[i].charAt(j)!=','){
                        output[i]+=input[i].charAt(j);

                        j++;
                    }
                    output[i]+=" ";
                }
            }

        }

        return output;
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent;
        intent = new Intent(getApplicationContext(), NavigationActivity.class);
        intent.putExtra("route",routes[position]);
        startActivity(intent);

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
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


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(myPosition.latitude, myPosition.longitude), 15));

        mMap.addPolyline(parseRout(routes[0],Color.argb(255, 102, 0, 204)));
        mMap.addPolyline(parseRout(routes[1],Color.argb(255,0,255,0)));
        mMap.addPolyline(parseRout(routes[2],Color.argb(255,255,0,0)));
        mMap.addPolyline(parseRout(routes[3],Color.argb(255, 0, 0, 0)));
    }

    public PolylineOptions parseRout(String route, int color) {

        ArrayList<String> latitudes = new ArrayList<String>();
        ArrayList<String> lontitudes = new ArrayList<String>();



            for (int i = 0; i < route.length(); i++) {
                if (route.charAt(i) == 'l' && route.charAt(i + 1) == 'e' && route.charAt(i + 2) == 'n') {
                    break;
                }
                String latitude = "";
                String lontitude = "";
                if (route.charAt(i) == 'l' && route.charAt(i + 1) == 'a' && route.charAt(i + 2) == 't') {
                    i = i + 8;
                    for (int j = 0; j < 8; j++) {
                        if (j == 2) {
                            latitude += ".";
                        }
                        i++;
                        latitude += route.charAt(i);

                    }
                    latitudes.add(latitude);
                }
                if (route.charAt(i) == 'l' && route.charAt(i + 1) == 'o' && route.charAt(i + 2) == 'n') {
                    i = i + 8;
                    for (int j = 0; j < 8; j++) {
                        if (j == 2) {
                            lontitude += ".";
                        }
                        i++;
                        lontitude += route.charAt(i);

                    }
                    lontitudes.add(lontitude);
                }

            }
            PolylineOptions line = new PolylineOptions();
            for (int i = 0; i < latitudes.size(); i++) {

                line.geodesic(true).add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(lontitudes.get(i))));
                System.out.println(Double.parseDouble(latitudes.get(i)) + " " + Double.parseDouble(lontitudes.get(i)));
            }
            line.color(color);
            //line.color(Color.argb(255, 102, 0, 204));
            //line.color(Color.argb(255,0,255,0));
            //line.color(Color.argb(255,255,0,0));
            //line.color(Color.argb(255,0,0,0));
            //mMap.addPolyline(line);
        return line;


    }

}
class SingleRow{
    String title;
    String description;

    SingleRow(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
class MyAdapter extends BaseAdapter{

    ArrayList<SingleRow> list;
    Context context;

    MyAdapter(ArrayList<SingleRow> list, Context c) {
        this.list = list;
        this.context = c;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
       return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.single_row_route,parent,false);
        TextView title = (TextView) row.findViewById(R.id.textView4);
        TextView descriptions = (TextView) row.findViewById(R.id.textView5);

        SingleRow temp =list.get(position);
        title.setText(temp.title);
        descriptions.setText(temp.description);

        return row;
    }


}
