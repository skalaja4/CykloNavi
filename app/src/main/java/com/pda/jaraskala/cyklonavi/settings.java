package com.pda.jaraskala.cyklonavi;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class settings extends ActionBarActivity {
    private static RadioButton rb;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private RadioButton rb5;
    private RadioButton rb6;
    private RadioButton rb7;

    public RadioButton getRb() {
        return rb;
    }

    public RadioButton getRb2() {
        return rb2;
    }

    public RadioButton getRb3() {
        return rb3;
    }

    public RadioButton getRb4() {
        return rb4;
    }

    public RadioButton getRb5() {
        return rb5;
    }

    public RadioButton getRb6() {
        return rb6;
    }

    public RadioButton getRb7() {
        return rb7;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        rb2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rb2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        reWriteSettings();
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public static boolean enSettings(){
        if(rb.isSelected()){
            return true;
        }else{
            return false;
        }
    }

    public void reWriteSettings(){

        String FILENAME = "cykloNaviSettings";
        String string = "";
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            string += "1"+rb.isChecked()+"2"+rb3.isChecked()+"3";
            if(rb5.isChecked()){
                string+="pointer1";
            }
            if(rb6.isChecked()){
                string+="pointer2";
            }if(rb7.isChecked()){
                string+="pointer3";
            }
            fos.write(string.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
