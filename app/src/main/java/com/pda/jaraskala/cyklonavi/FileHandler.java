package com.pda.jaraskala.cyklonavi;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by jaraskala on 20.04.15.
 */
public class FileHandler {

    FileOutputStream out;
    FileInputStream  in;



    public FileHandler(FileOutputStream out, FileInputStream in) {
        this.out = out;
        this.in = in;
    }


}
