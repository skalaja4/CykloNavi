package com.pda.jaraskala.cyklonavi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jaraskala on 15.05.15.
 */
public class Container {
    private LatLng myPosition;
    private LatLng direction;
    private Route[] routes = new Route[4];




        public Container(LatLng myPosition, LatLng direction, Route route1, Route route2, Route route3, Route route4) {
        this.myPosition = myPosition;
        this.direction = direction;
        routes[0] = route1;
        routes[1] = route2;
        routes[2] = route3;
        routes[3] = route4;

    }

    public LatLng getMyPosition() {
        return myPosition;
    }

    public void setMyPosition(LatLng myPosition) {
        this.myPosition = myPosition;
    }

    public LatLng getDirection() {
        return direction;
    }

    public void setDirection(LatLng direction) {
        this.direction = direction;
    }

    public Route[] getRoutes() {
        return routes;
    }

    public void setRoutes(Route[] routes) {
        this.routes = routes;
    }
}
