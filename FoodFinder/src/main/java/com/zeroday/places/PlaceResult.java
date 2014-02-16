package com.zeroday.places;

import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nick on 2/2/14.
 */
public class PlaceResult implements Serializable {

    @Key
    public String status;

    @Key
    public List<Place> results;

    @Key
    public String next_page_token;
}
