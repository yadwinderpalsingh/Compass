package net.yadwindersingh.missioncontrol;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, SensorEventListener  {

    MissionControl missionControl = new MissionControl();

    // device sensor manager
    private SensorManager mSensorManager;

    // location manager
    LocationManager myLocationManager;
    Location location;
    String PROVIDER = LocationManager.GPS_PROVIDER;

    // All screen control definitions
    ImageView compassImgView;
    TextView directionHeading;
    TextView txtLatitude;
    TextView txtLongitude;
    SeekBar distanceBar;
    TextView distanceBarValue;


    // Firebase database definition
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // All screen controls
        compassImgView = (ImageView) findViewById(R.id.imageViewCompass);
        directionHeading = (TextView) findViewById(R.id.tvHeading);
        distanceBar = (SeekBar) findViewById(R.id.seekbarDistance);
        distanceBar.setOnSeekBarChangeListener(this);
        distanceBarValue = (TextView) findViewById(R.id.distanceValue);
        txtLatitude = (TextView) findViewById(R.id.gpsLatitude);
        txtLongitude = (TextView) findViewById(R.id.gpsLongitude);
        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // update current location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        location = myLocationManager.getLastKnownLocation(PROVIDER);
        missionControl.showCurrentLocationCoordinates(location, txtLatitude, txtLongitude);
    }

    // save location trigger
    public void saveLocation(View v){

        missionControl.getTargetCoordAndSave(location, distanceBar, getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        // resume location update
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocationManager.requestLocationUpdates(PROVIDER, 0, 0, myLocationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);

        // pause location update
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocationManager.removeUpdates(myLocationListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated and update Compass
        missionControl.updateCompass(Math.round(event.values[0]), directionHeading, compassImgView);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        distanceBarValue.setText(String.valueOf(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private LocationListener myLocationListener  = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            missionControl.showCurrentLocationCoordinates(location, txtLatitude, txtLongitude);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    };
}
