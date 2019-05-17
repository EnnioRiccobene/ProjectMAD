package com.madgroup.sdk;

import java.io.Serializable;

public class Position {
    String latitude;
    String longitude;

    public Position(){}

    public Position(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
