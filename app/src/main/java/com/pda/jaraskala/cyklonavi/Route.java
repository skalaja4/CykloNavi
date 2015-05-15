package com.pda.jaraskala.cyklonavi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by jaraskala on 15.05.15.
 */
public class Route{
    ArrayList<LatLng> points;
    float length=0;
    float duration=0;
    float ascent=0;

    public Route(float length, float duration, float ascent) {
        this.length = length;
        this.duration = duration;
        this.ascent = ascent;
        points = new ArrayList<LatLng>();
    }
    public Route(){
        points = new ArrayList<LatLng>();
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<LatLng> points) {
        this.points = points;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getAscent() {
        return ascent;
    }

    public void setAscent(float ascent) {
        this.ascent = ascent;
    }

}
