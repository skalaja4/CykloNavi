package com.pda.jaraskala.cyklonavi;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import com.google.android.gms.maps.model.LatLng;

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
