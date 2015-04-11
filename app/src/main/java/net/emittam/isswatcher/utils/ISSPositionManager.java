package net.emittam.isswatcher.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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

    public void updatePositionData(final UpdateFinishListener updateFinishListener) {
        mRequestQueue.add(new StringRequest(POSITION_API_URL,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String json = response.substring("jsonp(".length(), response.length() - 1);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray positionArray = jsonObject.getJSONArray("orbits");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("JST"));
                    mPositions.clear();
                    for (int i = 0; i < positionArray.length(); i++) {
                        JSONObject positionObject = positionArray.getJSONObject(i);
                        double lat = positionObject.getDouble("latitude");
                        double lng = positionObject.getDouble("longitude");
                        Date date = dateFormat.parse(positionObject.getString("date"));
                        Position p = new Position(date, lat, lng);
                        mPositions.add(p);
                    }
                    updateFinishListener.onUpdateFinished(mPositions);
                } catch (JSONException e) {
                    Log.e("error", "error " + e);
                } catch (ParseException e) {
                    Log.e("error", "error " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("ISSWacher", "error " + error.toString());
            }
        }));

    }

    public Position nowPosition() {
        Date now = new Date();
        for (final Position p : mPositions) {
            if (p.date.after(now)) {
                return p;
            }
        }
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
        public final Date date;

        public Position(Date date, double lat, double lng) {
            this.date = date;
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    ", date=" + date +
                    '}';
        }
    }

    public interface UpdateFinishListener {
        public void onUpdateFinished(List<Position> updatedPositionList);
    }
}
