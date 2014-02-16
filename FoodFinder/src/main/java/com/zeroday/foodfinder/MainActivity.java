package com.zeroday.foodfinder;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zeroday.common.Constants;
import com.zeroday.places.Place;
import com.zeroday.places.PlaceSearch;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements
            GooglePlayServicesClient.ConnectionCallbacks,
            GooglePlayServicesClient.OnConnectionFailedListener,
            GoogleMap.OnMarkerClickListener {

        private static final Logger LOGGER = Logger.getLogger(PlaceholderFragment.class.getName());
        private GoogleMap map;
        private LocationClient locationClient;

        // User input fields
        private ImageButton radiusSearch;
        private EditText radiusText;
        private SeekBar radiusSeek;

        // circle parameters
        private static final double METER_CONVERSION = 1609.34;
        private LatLng pressLocation;
        private int radius;
        private Circle searchArea;

        private Marker marker;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Create the location client
            locationClient = new LocationClient(getActivity(), this, this);

            // Get a handle to the map and set the default properties
            map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
            map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
            map.setBuildingsEnabled(false);
            map.setMyLocationEnabled(true);

            // Set the callback for long clicks
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    pressLocation = latLng;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pressLocation, map.getCameraPosition().zoom);
                    map.animateCamera(cameraUpdate);
                    searchArea.setCenter(pressLocation);
                    searchArea.setVisible(true);
                }
            });
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    searchArea.setVisible(false);
                }
            });
            map.setOnMarkerClickListener(this);

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            locationClient.connect();
            if (radius == 0) {
                radius = getResources().getInteger(R.integer.radius_value);
            }
            final PlaceholderFragment that = this;

            // Set up the search button
            radiusSearch = ((ImageButton) getActivity().findViewById(R.id.radius_search));
            radiusSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pressLocation != null) {
                        PlaceSearch search = new PlaceSearch();
                        search.key = Constants.API_KEY;
                        search.latitude = pressLocation.latitude;
                        search.longitude = pressLocation.longitude;
                        search.radius = radius * METER_CONVERSION;
                        search.keyword = parseKeywords(((EditText) getActivity().findViewById(R.id.search_keyword)).getText().toString());

                        Intent intent = new Intent(getActivity(), SelectionsActivity.class);
                        intent.putExtra("PlaceSearch", search);
                        startActivityForResult(intent, 1);
                    }
                }
            });

            // Set up the radius entry field
            radiusText = ((EditText) getActivity().findViewById(R.id.radius));
            radiusText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if ("".equals(charSequence.toString())) {
                        radius = 0;
                    } else {
                        radius = Integer.parseInt(charSequence.toString());
                    }

                    radiusSeek.setProgress(radius);
                    searchArea.setRadius(radius * METER_CONVERSION);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            // Set up the radius seek bar
            radiusSeek = ((SeekBar) getActivity().findViewById(R.id.radius_seek));
            radiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    radiusText.setText(Integer.valueOf(i).toString());
                    radius = i;
                    searchArea.setRadius(radius * METER_CONVERSION);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("place")) {
                    if (marker != null) {
                        marker.remove();
                    }

                    Place place = (Place) data.getExtras().get("place");
                    LatLng location = new LatLng(place.geometry.location.lat, place.geometry.location.lng);
                    marker = map.addMarker(new MarkerOptions()
                            .position(location)
                            .title(place.name)
                            .snippet(place.vicinity)
                    );
                    marker.showInfoWindow();
                }
            }
        }

        private String parseKeywords(String text) {
            if (text == null || "".equals(text.trim())) {
                return null;
            }

            String[] keywords = text.split(",");
            List<String> parsedKeywords = new ArrayList<String>(keywords.length);
            for (String keyword : keywords) {
                keyword = keyword.trim();
                if (keyword.indexOf(" ") > 0) {
                    keyword.replaceAll(" ", "+");
                    keyword = "\"" + keyword + "\"";
                }
                parsedKeywords.add(keyword);
            }
            return StringUtils.join(parsedKeywords, "+");
        }

        /*
         * Called by Location Services when the request to connect the
         * client finishes successfully. At this point, you can
         * request the current location or start periodic updates
         */
        @Override
        public void onConnected(Bundle dataBundle) {
            // Display the connection status
            Location location = locationClient.getLastLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, getResources().getInteger(R.integer.default_zoom));
            map.moveCamera(cameraUpdate);

            if (searchArea == null) {
                CircleOptions options = new CircleOptions()
                        .center(latLng)
                        .radius(radius * METER_CONVERSION)
                        .fillColor(Color.argb(100, 114, 135, 135))
                        .strokeColor(Color.BLACK)
                        .strokeWidth(2)
                        .visible(false);

                searchArea = map.addCircle(options);
            }
        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            Location from = locationClient.getLastLocation();
            LatLng to = marker.getPosition();
            Uri uri = null;
            if (marker.getSnippet() == null || marker.getSnippet().trim().isEmpty()) {
                uri = Uri.parse("google.navigation:q=" + to.latitude + "," + to.longitude + "(" + marker.getTitle().replaceAll(" ", "+") + ")");
            } else {
                uri = Uri.parse("google.navigation:q=" + marker.getSnippet().replaceAll(" ", "+") + "(" + marker.getTitle().replaceAll(" ", "+") + ")");
            }

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return false;
        }
    }


}
