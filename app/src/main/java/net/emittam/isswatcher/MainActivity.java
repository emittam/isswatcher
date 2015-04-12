package net.emittam.isswatcher;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.emittam.isswatcher.utils.ISSPositionManager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    // GoogleMapオブジェクトの宣言
    private GoogleMap googleMap;

    private Timer mTimer;

    private Marker mMarker;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MapFragmentオブジェクトを取得
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        try {
            // GoogleMapオブジェクトの取得
            googleMap = mapFragment.getMap();

            // Activityが初回で生成されたとき
            if (savedInstanceState == null) {

                // MapFragmentのオブジェクトをセット
                mapFragment.setRetainInstance(true);

                // 地図の初期設定を行うメソッドの呼び出し
                mapInit();
            }
        }
        // GoogleMapが使用不可のときのためにtry catchで囲っています。
        catch (Exception e) {
        }

        ISSPositionManager.getInstance(this).updatePassTime(35.57, 136.11, new ISSPositionManager.UpdatePassDateFinishListener() {
            @Override
            public void onUpdateFinished(Date date) {
                Log.d("debug", "pass : " + date.toString());
                Intent intent = new Intent(MainActivity.this, PushBroadcastReceiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(), 0, sender);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ISSPositionManager.getInstance(this).updatePositionData(new ISSPositionManager.UpdatePositionFinishListener() {
            @Override
            public void onUpdateFinished(ISSPositionManager.Position updatedPosition) {
                CameraPosition camerapos = new CameraPosition.Builder()
                        .target(new LatLng(updatedPosition.lat, updatedPosition.lng)).zoom(2.5f).build();

                // 地図の中心の変更する
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ISSPositionManager.getInstance(MainActivity.this).updatePositionData(new ISSPositionManager.UpdatePositionFinishListener() {
                            @Override
                            public void onUpdateFinished(ISSPositionManager.Position updatedPosition) {
                                mMarker.setPosition(new LatLng(updatedPosition.lat, updatedPosition.lng));
                            }
                        });

                    }
                }, 0, 5000);
            }

        });
    }

    // 地図の初期設定メソッド
    private void mapInit() {

        // 地図タイプ設定
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示を行なう
        googleMap.setMyLocationEnabled(true);

        //マーカー
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(0, 0));
        this.mMarker = googleMap.addMarker(options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
