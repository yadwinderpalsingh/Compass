package net.yadwindersingh.missioncontrol;

import android.content.Context;
import android.location.Location;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

/**
 * Created by yadwinderpalsingh on 2016-12-04.
 */

public class MissionControl {

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // record the bearing
    private int bearingRange;

    // Firebase database
    private FirebaseDatabase db;

    private static final double DegreesToRadians = Math.PI/180.0;
    private static final double RadiansToDegrees = 180.0/ Math.PI;
    private static final double EarthRadius = 6378137.0;

    // shows success toast
    public void showToast(Context context){

        CharSequence text = "Target Saved Successfully";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // generates random uuid
    public String randomStringUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public void updateCompass(float degree, TextView directionHead, ImageView compassImgView){

        updateDirection(degree, directionHead);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        compassImgView.startAnimation(ra);
        currentDegree = -degree;
    }

    // show current location
    public void showCurrentLocationCoordinates(Location l, TextView txtLatitude, TextView txtLongitude) {
        if (l != null) {
            txtLatitude.setText(Double.toString(l.getLatitude()));
            txtLongitude.setText(Double.toString(l.getLongitude()));
        }
    }

    // update direction user facing towards
    public void updateDirection(double bearing, TextView directionHead) {
        int range = (int) (bearing / (360f / 16f));
        bearingRange = (int) bearing;
        String dirTxt = "";

        if (range == 15 || range == 0)
            dirTxt = "N";
        if (range == 1 || range == 2)
            dirTxt = "NE";
        if (range == 3 || range == 4)
            dirTxt = "E";
        if (range == 5 || range == 6)
            dirTxt = "SE";
        if (range == 7 || range == 8)
            dirTxt = "S";
        if (range == 9 || range == 10)
            dirTxt = "SW";
        if (range == 11 || range == 12)
            dirTxt = "W";
        if (range == 13 || range == 14)
            dirTxt = "NW";

        directionHead.setText("" + ((int) bearing) + ((char) 176) + " " + dirTxt); // char 176 ) = degrees ...
    }

    // calculate target co-ordinates
    public void getTargetCoordAndSave(Location location, SeekBar distanceBar, Context context){
        double latA = location.getLatitude() * DegreesToRadians;
        double lonA = location.getLongitude() * DegreesToRadians;
        double angularDistance = distanceBar.getProgress() / EarthRadius;
        double trueCourse = bearingRange * DegreesToRadians;

        double targetLatitude = (Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance) * Math.cos(trueCourse))) * RadiansToDegrees;

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance) * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(targetLatitude));

        double targetLongitude = (((lonA + dlon + Math.PI) % (2 * Math.PI)) - Math.PI) * RadiansToDegrees;

        TargetLocation targetLocation = new TargetLocation();
        targetLocation.setCurrentLat(location.getLatitude());
        targetLocation.setCurrentLong(location.getLongitude());
        targetLocation.setTargetLat(targetLatitude);
        targetLocation.setTargetLong(targetLongitude);
        targetLocation.setBearing(bearingRange);

        saveTargetLocation(targetLocation, context);
    }

    // save target location to firebase
    private void saveTargetLocation(TargetLocation targetLocation, Context context){

        db = FirebaseDatabase.getInstance();

        // saves data in json
        DatabaseReference myRef = db.getReference(randomStringUUID());
        myRef.setValue(targetLocation);

        showToast(context);
    }
}
