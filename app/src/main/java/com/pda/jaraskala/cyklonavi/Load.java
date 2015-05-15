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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Load extends ActionBarActivity implements AdapterView.OnItemClickListener {
LatLng destination;
    Container container;
    LatLng myPosition;

    ArrayList<Row> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        list = new ArrayList<Row>();
        try {
            FileInputStream fin= openFileInput("saves");
            int read=-1;
            StringBuffer buffer = new StringBuffer();
            while((read=fin.read())!=-1){
                buffer.append((char)read);
            }
            fin.close();
            String input = buffer.toString();
            String tmp="";
            int j=0;

            ArrayList<String> names = new ArrayList<String>();
            ArrayList<String> latitudes = new ArrayList<String>();
            ArrayList<String> longtitudes= new ArrayList<String>();
            for(int i=0;i<input.length();i++){

                if(input.charAt(i)==' '){
                    if(j==0){
                        System.out.println("latitudes: "+tmp);
                        latitudes.add(tmp);
                        tmp="";
                    }
                    if(j==1){
                        System.out.println("longtitudes: "+tmp);
                        longtitudes.add(tmp);
                        tmp="";
                    }
                    if(j==2){
                        System.out.println("names: "+tmp);
                        names.add(tmp);
                        j=-1;
                        tmp="";
                    }


                 j++;
                }else{
                    tmp+=input.charAt(i);
                }




            }
            for(int i=0;i<names.size();i++){
            list.add(new Row(names.get(i),new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longtitudes.get(i)))));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        lv.setAdapter(new Adapter(list,this));

        Bundle extras = getIntent().getExtras();

        if(extras !=null){
            myPosition = (LatLng)extras.get("coordinates2");

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load, menu);
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

            NavUtils.navigateUpFromSameTask(this);
            return true;


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("KLIKNUTOoo");
        destination = list.get(position).getPosition();
        String[] routes = new String[4];
        try {
            routes = readRouts();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] parsed = parseInput(routes);

        Route[] route1234 = new Route[4];
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
            route1234[i]=new Route(routeDescription[0]/1000,routeDescription[1]/100,routeDescription[2]);

        }

        route1234[0].setPoints(parseRoutes(routes[0]));
        route1234[1].setPoints(parseRoutes(routes[1]));
        route1234[2].setPoints(parseRoutes(routes[2]));
        route1234[3].setPoints(parseRoutes(routes[3]));

        container= new Container(myPosition,destination,route1234[0],route1234[1],route1234[2],route1234[3]);



        startActivity(myIntent(RouteChooser.class));





    }
    public String[] readRouts() throws IOException {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://its.felk.cvut.cz/cycle-planner-1.1.3-SNAPSHOT-junctions/bicycleJourneyPlanning/planJourneys?startLon="+myPosition.longitude+"&startLat="+myPosition.latitude+"&endLon="+destination.longitude+"&endLat="+destination.latitude+"&avgSpeed=20");

        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));

        String[] output = new String[4];
        //cameraBorder=new String[4];
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
    public ArrayList<LatLng> parseRoutes(String route){
        ArrayList<String> latitudes = new ArrayList<String>();
        ArrayList<String> lontitudes = new ArrayList<String>();
        ArrayList<LatLng> output = new ArrayList<LatLng>();



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
            output.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(lontitudes.get(i))));

        }
        return output;
    }

    public Intent myIntent(Class c){
        Intent intent;
        intent = new Intent(getApplicationContext(), c);
        intent.putExtra("boolean",false);
        intent.putExtra("intent",new Intent(this, Load.class));
        intent.putExtra("coordinates2",container.getMyPosition());
        intent.putExtra("coordinates1",container.getDirection());

        for (int i = 0; i<4;i++){
            intent.putExtra("route" +i+"1", container.getRoutes()[i].getPoints());
            intent.putExtra("route" +i+"2", container.getRoutes()[i].getLength());
            intent.putExtra("route" +i+"3", container.getRoutes()[i].getDuration());
            intent.putExtra("route" +i+"4", container.getRoutes()[i].getAscent());

        }
        return intent;
    }
}
class Row{
    String title;
    LatLng position;



    Row(String title, LatLng position) {
        this.title = title;
        this.position=position;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }
}
    class Adapter extends BaseAdapter {

        ArrayList<Row> list;
        Context context;

        Adapter(ArrayList<Row> list, Context c) {
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
            View row = inflater.inflate(R.layout.load_row,parent,false);
            TextView title = (TextView) row.findViewById(R.id.textView8);

            Row temp =list.get(position);
            title.setText(" " + temp.title);


            return row;
        }
    }

