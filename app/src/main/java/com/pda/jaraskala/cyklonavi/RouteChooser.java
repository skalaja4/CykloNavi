package com.pda.jaraskala.cyklonavi;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RouteChooser extends ActionBarActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener{
    LatLng direction;
    LatLng myPosition;
    ListView listView;
    Route[] routes = new Route[4];
    String[] cameraBorder;
    Container container;
    ArrayList<PolylineOptions> lines = new ArrayList<PolylineOptions>();
    PopupWindow popupWindow;
    Button saveButton;
    //private LatLng myPosition;
    EditText et;
    private GoogleMap mMap;
    private Marker myMark=null;

    @Override
    protected void onResume() {
        super.onResume();
        //setContentView(R.layout.activity_route_chooser);
        listView=(ListView)findViewById(R.id.listViewRoute);
        listView.setOnItemClickListener(this);


        Bundle extras = getIntent().getExtras();
      //  System.out.println("JOOO");
        if(extras !=null){
       //     System.out.println("ANOOO");
           // direction = (LatLng) extras.get("coordinates1");
           // myPosition = (LatLng) extras.get("coordinates2");

            for(int i=0;i<4;i++){
                routes[i]=new Route();
                routes[i].points=(ArrayList<LatLng>)extras.get("route" +i+"1");
                routes[i].length=(float)extras.get("route" +i+"2");
                routes[i].duration=(float)extras.get("route" +i+"3");
                routes[i].ascent=(float)extras.get("route" +i+"4");
            }
            container = new Container((LatLng)extras.get("coordinates2"),(LatLng)extras.get("coordinates1"),routes[0],routes[1],routes[2],routes[3]) ;
            direction=container.getDirection();
            myPosition=container.getMyPosition();

          //  System.out.println(direction.latitude);
           // System.out.println(myPosition.latitude);


        }
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //myPosition=new LatLng(50.078455,14.400039);





        /*String[] input = new String[4];

        try {
            input = readRouts();
        } catch (IOException e) {
            e.printStackTrace();
        }
        routes=input;
        String parsed[] = parseInput(input);


*/

        ArrayList<SingleRow> arrayList = new ArrayList<SingleRow>();


        Resources res = getResources();
        String[] titles = res.getStringArray(R.array.typeRouteTitles);
        int[] colors={Color.argb(255, 102, 0, 204),Color.argb(255,0,255,0),Color.argb(255,255,0,0),Color.argb(255,0,0,0)};



        for(int i=0;i<4;i++){
            arrayList.add(new SingleRow(titles[i],container.getRoutes()[i].getLength()+" km, "+(int)container.getRoutes()[i].getDuration() +" min, ascent: "+container.getRoutes()[i].getAscent()+" m",colors[i]));

        }


        listView.setAdapter(new MyAdapter(arrayList,this,container.getRoutes(),container));


        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_chooser);

       // BitmapDescriptor icon= BitmapDescriptorFactory.fromResource(R.drawable.marker);
        //myMark=mMap.addMarker(new MarkerOptions().position(myPosition).title("position").icon(icon));

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

        if(id==R.id.action_save){

            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_save,null);
            popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

            Geocoder gCoder = new Geocoder(this);
            try {
                List<Address> addresses = gCoder.getFromLocation(direction.latitude,direction.longitude,1);
               // LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //final View view=inflater.inflate(R.layout.popup_save,null);
                TextView tv = (TextView) popupView.findViewById(R.id.textView7);
                et = (EditText) popupView.findViewById(R.id.editText);
                if(addresses!= null && addresses.size()>0){

                    tv.setText(addresses.get(0).getThoroughfare()+" "+addresses.get(0).getFeatureName());
                }
                popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER,0,0);
                saveButton = (Button)popupView.findViewById(R.id.save);
                popupWindow.setFocusable(true);
                popupWindow.update();
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuffer buffer = new StringBuffer();
                        try {
                            FileInputStream fin = openFileInput("saves");
                            int read=-1;

                            while((read=fin.read())!=-1){
                                buffer.append((char)read);
                            }


                            fin.close();
                          //  System.out.println(buffer);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try{
                            FileOutputStream fos = openFileOutput("saves", Context.MODE_PRIVATE);
                            String latitude = direction.latitude+" ";
                            String lontitude = direction.longitude+" ";
                            String name = et.getText().toString()+" ";
                            String list = buffer.toString();
                            fos.write(list.getBytes());
                            fos.write(latitude.getBytes());
                            fos.write(lontitude.getBytes());
                            fos.write(name.getBytes());

                            fos.close();




                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        popupWindow.dismiss();
                    }
                });

               Button cancel= (Button)popupView.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }



            return true;
        }

        if (id == R.id.action_settings_settings) {
            startActivity(myIntent(Setting.class));


            return true;
        }
        if(id==R.id.action_settings_help){

            startActivity(myIntent(Help.class));
            return true;
        }

        if(id == android.R.id.home){

            NavUtils.navigateUpFromSameTask(this);
            return true;


        }

        return super.onOptionsItemSelected(item);
    }




    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2))
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


        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myPosition.latitude, myPosition.longitude), 15));



        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(rohy(),500,500,0));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(rohy(),100,100,5));


        int[] colors = {Color.argb(255, 102, 0, 204), Color.argb(255, 0, 255, 0),Color.argb(255,255,0,0),Color.argb(255, 0, 0, 0)};
        for(int j =0; j<4;j++) {
            PolylineOptions line = new PolylineOptions();
            for (int i = 0; i < container.getRoutes()[j].points.size(); i++) {

                line.geodesic(true).add(container.getRoutes()[j].points.get(i));


            }
            line.color(colors[j]);
            lines.add(line);
            mMap.addPolyline(line);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        mMap.clear();
        for(int i=0; i<4;i++){
        mMap.addPolyline(lines.get(i));
        }
        mMap.addPolyline(lines.get(position));
    }
    public LatLngBounds rohy(){

        double myLat =myPosition.latitude;
        double myLon = myPosition.longitude;
        double desLat =direction.latitude;
        double desLon =direction.longitude;


        LatLng southWest = new LatLng(Math.min(myLat,desLat),Math.min(myLon,desLon));
        LatLng northEast = new LatLng(Math.max(myLat,desLat),Math.max(myLon,desLon));

     //   System.out.println(southWest.latitude);
       // System.out.println(southWest.longitude);
        //System.out.println(northEast.latitude);
        //System.out.println(northEast.longitude);

        return new LatLngBounds(southWest,northEast);

    }
    public Intent myIntent(Class c){
        Intent intent;
        intent = new Intent(getApplicationContext(), c);

        intent.putExtra("boolean",false);
        intent.putExtra("coordinates2",container.getMyPosition());
        intent.putExtra("coordinates1",container.getDirection());
        intent.putExtra("intent",new Intent(this, RouteChooser.class));
        for (int i = 0; i<4;i++){
            intent.putExtra("route" +i+"1", container.getRoutes()[i].getPoints());
            intent.putExtra("route" +i+"2", container.getRoutes()[i].getLength());
            intent.putExtra("route" +i+"3", container.getRoutes()[i].getDuration());
            intent.putExtra("route" +i+"4", container.getRoutes()[i].getAscent());

        }
        return intent;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        popupWindow.dismiss();
    }

    public void showDialog(){
        FragmentManager manager = getFragmentManager();
        SaveDialog dialog=new SaveDialog();
        dialog.show(manager,"SaveDialog");
    }






}
class SingleRow{
    String title;
    String description;
    int color;

    SingleRow(String title, String description, int color) {
        this.title = title;
        this.description = description;
        this.color=color;

    }
}
class MyAdapter extends BaseAdapter{

    ArrayList<SingleRow> list;
    Context context;
    Route[] routes;
    Container container;

    MyAdapter(ArrayList<SingleRow> list, Context c, Route[] routes, Container container) {
        this.list = list;
        this.context = c;
        this.routes=routes;
        this.container=container;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.single_row_route,parent,false);
        TextView title = (TextView) row.findViewById(R.id.textView4);
        TextView descriptions = (TextView) row.findViewById(R.id.textView5);
        TextView icon = (TextView) row.findViewById(R.id.textView6);
        Button button = (Button) row.findViewById(R.id.navigate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ArrayList<LatLng> points = routes[position].points;

                Intent intent;
                intent = new Intent(context.getApplicationContext(), NavigationArrow.class);
                intent.putExtra("points",points);
                intent.putExtra("boolean",false);
                intent.putExtra("coordinates2",container.getMyPosition());
                intent.putExtra("coordinates1",container.getDirection());
                intent.putExtra("intent",new Intent(context, RouteChooser.class));
                for (int i = 0; i<4;i++){
                    intent.putExtra("route" +i+"1", container.getRoutes()[i].getPoints());
                    intent.putExtra("route" +i+"2", container.getRoutes()[i].getLength());
                    intent.putExtra("route" +i+"3", container.getRoutes()[i].getDuration());
                    intent.putExtra("route" +i+"4", container.getRoutes()[i].getAscent());

                }


                context.startActivity(intent);
            }
        });


        SingleRow temp =list.get(position);
        title.setText(" "+temp.title);
        descriptions.setText(" "+temp.description);
        icon.setBackgroundColor(temp.color);


        return row;
    }


}
