package com.zeroday.places;

import java.io.Serializable;

/**
 * Created by nick on 2/2/14.
 */
public class PlaceSearch implements Serializable {
    public String key;
    public double latitude;
    public double longitude;
    public double radius;
    public String keyword;
    public String types = "restaurant";
    public String pageToken;
}
