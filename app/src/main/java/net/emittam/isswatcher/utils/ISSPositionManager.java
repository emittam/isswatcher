package net.emittam.isswatcher.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by emittam on 15/04/12.
 */
public final class ISSPositionManager {

    private static ISSPositionManager INSTANCE = null;

    private RequestQueue mRequestQueue;

    private Position mPosition;

    private ISSPositionManager(Context context) {
        this.mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.mRequestQueue.start();
    }

    private static final String POSITION_API_URL = "http://api.open-notify.org/iss-now.json";

    public void updatePositionData(final UpdatePositionFinishListener updatePositionFinishListener) {
        mRequestQueue.add(new StringRequest(POSITION_API_URL,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.w("debug", jsonObject.toString());
                    double lat = jsonObject.getJSONObject("iss_position").getDouble("latitude");
                    double lng = jsonObject.getJSONObject("iss_position").getDouble("longitude");
                    Date date = new Date(jsonObject.getLong("timestamp"));
                    mPosition = new Position(date, lat, lng);
                    updatePositionFinishListener.onUpdateFinished(mPosition);
                } catch (JSONException e) {
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

    private static final String PASS_TIME_API_URL = "http://api.open-notify.org/iss-pass.json?lat=%s&lon=%s";

    public void updatePassTime(double lat, double lng , final UpdatePassDateFinishListener listener) {
        mRequestQueue.add(new JsonObjectRequest(String.format(PASS_TIME_API_URL, lat, lng), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    long time = response.getJSONArray("response").getJSONObject(0).getLong("risetime");
                    Date date = new Date(time);
                    listener.onUpdateFinished(date);
                } catch (JSONException e) {
                    Log.e("error", "jsonExecption : " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    public Position nowPosition() {
        return mPosition;
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

    public interface UpdatePositionFinishListener {
        public void onUpdateFinished(Position updatedPosition);
    }

    public interface  UpdatePassDateFinishListener {
        public void  onUpdateFinished(Date date);
    }
}
