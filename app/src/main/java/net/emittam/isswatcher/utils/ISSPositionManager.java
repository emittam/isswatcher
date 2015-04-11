package net.emittam.isswatcher.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by emittam on 15/04/12.
 */
public final class ISSPositionManager {

    private static ISSPositionManager INSTANCE = null;

    private List<Position> mPositions;

    private RequestQueue mRequestQueue;

    private ISSPositionManager(Context context) {
        this.mPositions = new ArrayList<>();
        this.mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.mRequestQueue.start();
    }

    private static final String POSITION_API_URL = "http://tsujimotter.info/api/SateliteTracker/orbitjsonp.cgi?callback=jsonp";

    public void updatePositionData() {
        mRequestQueue.add(new StringRequest(POSITION_API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    response.replace("json(", "");
                    response.replace("})", "}");
                }
                Log.w("ISSWatcher", "response : " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));

    }

    public Position nowPosition() {
        return null;
    }

    public static synchronized ISSPositionManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ISSPositionManager(context);
        }
        return INSTANCE;
    }


    public static class Position {
        public final double lat;
        public final double lng;

        public Position(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
}
