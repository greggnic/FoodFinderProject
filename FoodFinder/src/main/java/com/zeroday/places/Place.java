package com.zeroday.places;

import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by nick on 2/2/14.
 */
public class Place implements Serializable {
    @Key
    public String vicinity;

    @Key
    public String icon;

    @Key
    public String name;

    @Key
    public String[] types;

    @Key
    public Geometry geometry;

    public static class Geometry implements Serializable {
        @Key
        public Location location;

        @Override
        public String toString() {
            return "Geometry{" +
                    "location=" + location +
                    '}';
        }
    }

    public static class Location implements Serializable {
        @Key
        public double lat;

        @Key
        public double lng;

        @Override
        public String toString() {
            return "Location{" +
                    "lat='" + lat + '\'' +
                    ", lng='" + lng + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Place{" +
                "vicinity='" + vicinity + '\'' +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", types=" + Arrays.toString(types) +
                ", geometry=" + geometry +
                '}';
    }
}
