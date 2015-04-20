package com.pda.jaraskala.cyklonavi;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by jaraskala on 20.04.15.
 */
public class FileHandler {
    String FILENAME = "CykloNaviSettings";
    FileOutputStream out;
    FileInputStream  in;

    public FileHandler() {


        this.in = fileContext.getApplicationContext().openFileInput(FILENAME, Context.MODE_PRIVATE);
    }
}
