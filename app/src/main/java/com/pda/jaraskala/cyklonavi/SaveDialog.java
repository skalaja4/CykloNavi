package com.pda.jaraskala.cyklonavi;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;

public class SaveDialog extends DialogFragment {
    Button cancelBut, saveBut;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.popup_save,null);
        cancelBut = (Button) view.findViewById(R.id.cancel);
        saveBut = (Button) view.findViewById(R.id.save);




        return view;
    }


}
