package net.emittam.isswatcher;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.emittam.isswatcher.utils.ISSPositionManager;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    // GoogleMapオブジェクトの宣言
    private GoogleMap googleMap;

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
    }

    // 地図の初期設定メソッド
    private void mapInit() {

        // 地図タイプ設定
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示を行なう
        googleMap.setMyLocationEnabled(true);

        ISSPositionManager.getInstance(this).updatePositionData(new ISSPositionManager.UpdateFinishListener() {
            @Override
            public void onUpdateFinished(List<ISSPositionManager.Position> updatedPositionList) {
                Log.w("debug", ISSPositionManager.getInstance(MainActivity.this).nowPosition().toString());
                ISSPositionManager.Position p = ISSPositionManager.getInstance(MainActivity.this).nowPosition();
                // めがね会館の位置、ズーム設定
                CameraPosition camerapos = new CameraPosition.Builder()
                        .target(new LatLng(p.lat,p.lng)).zoom(1.5f).build();

                // 地図の中心の変更する
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
                //マーカー
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(p.lat,p.lng));
                googleMap.addMarker(options);
            }
        });

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
