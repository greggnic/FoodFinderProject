package com.zeroday.foodfinder;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.zeroday.adapter.PlaceResultsAdapter;
import com.zeroday.common.Constants;
import com.zeroday.places.Place;
import com.zeroday.places.PlaceResult;
import com.zeroday.places.PlaceSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Logger;

public class SelectionsActivity extends ListActivity {

    public View header;
    public PlaceResultsAdapter adapter;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        header = getLayoutInflater().inflate(R.layout.list_header, null);
        listView = getListView();
        listView.addHeaderView(header);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Bundle extras = getIntent().getExtras();
        adapter = new PlaceResultsAdapter(this, R.layout.list_item, new ArrayList<Place>());
        setListAdapter(adapter);

        final SelectionsActivity that = this;
        new PlaceRunner(this).execute((PlaceSearch) extras.get("PlaceSearch"));
        ((Button) header.findViewById(R.id.load_more)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceSearch search = new PlaceSearch();
                search.pageToken = adapter.next_page_token;
                search.key = Constants.API_KEY;

                new PlaceRunner(that).execute(search);
            }
        });

        ((Button) header.findViewById(R.id.randomizer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int random = (int) (Math.random() * adapter.getCount());
                listView.setItemChecked(random, true);
                listView.setSelection(random);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        listView.setItemChecked(position, true);

        Intent data = new Intent();
        data.putExtra("place", adapter.getItem(position - 1));
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * Created by nick on 2/2/14.
     */
    public static class PlaceRunner extends AsyncTask<PlaceSearch, Void, PlaceResult> {
        private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
        private static final Logger LOGGER = Logger.getLogger(PlaceRunner.class.getName());
        private static final HttpTransport TRANSPORT = new ApacheHttpTransport();

        private SelectionsActivity selectionsActivity;

        public PlaceRunner(SelectionsActivity selectionsActivity) {
            this.selectionsActivity = selectionsActivity;
        }

        @Override
        protected PlaceResult doInBackground(PlaceSearch... placeSearches) {
            try {
                return performSearch(placeSearches[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private PlaceResult performSearch(PlaceSearch search) throws IOException {
            HttpRequestFactory factory = createRequestFactory(TRANSPORT);
            HttpRequest request = factory.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));

            if (search.pageToken != null) {
                request.getUrl().put("key", search.key);
                request.getUrl().put("pagetoken", search.pageToken);
                request.getUrl().put("sensor", "false");
            } else {
                request.getUrl().put("key", search.key);
                request.getUrl().put("location", search.latitude + "," + search.longitude);
                request.getUrl().put("radius", search.radius);
                request.getUrl().put("types", search.types);
                if (search.keyword != null) {
                    request.getUrl().put("keyword", search.keyword);
                }
                request.getUrl().put("sensor", "false");
            }

            LOGGER.info("RESPONSE FROM WEB SERVICE: " + request.getUrl().toString());
            return request.execute().parseAs(PlaceResult.class);
        }

        private HttpRequestFactory createRequestFactory(final HttpTransport transport) {

            return transport.createRequestFactory(new HttpRequestInitializer() {
                public void initialize(HttpRequest request) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setUserAgent("LunchDecider-Test");
                    request.setHeaders(headers);
                    JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
                    request.setParser(parser);
                }
            });
        }

        @Override
        protected void onPostExecute(PlaceResult placeResult) {
            super.onPostExecute(placeResult);
            if (placeResult != null) {
                for (Place place : placeResult.results) {
                    LOGGER.info(place.toString());
                }
            }
            selectionsActivity.adapter.addAll(placeResult.results);
            selectionsActivity.adapter.sort(new Comparator<Place>() {
                @Override
                public int compare(Place place, Place place2) {
                    return place.name.compareTo(place2.name);
                }
            });
            selectionsActivity.adapter.next_page_token = placeResult.next_page_token;
            if (placeResult.next_page_token == null) {
                selectionsActivity.header.findViewById(R.id.load_more).setVisibility(View.GONE);
            } else {
                selectionsActivity.header.findViewById(R.id.load_more).setVisibility(View.VISIBLE);
            }
        }
    }

}
