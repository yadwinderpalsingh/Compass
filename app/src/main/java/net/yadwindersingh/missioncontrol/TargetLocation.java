package net.yadwindersingh.missioncontrol;

/**
 * Created by yadwinderpalsingh on 2016-12-04.
 */

public class TargetLocation {

    public TargetLocation() {

    }

    private double currentLat;
    private double currentLong;
    private double targetLat;
    private double targetLong;
    private int bearing;



    //Getters and setters
    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double lat) {
        this.currentLat = lat;
    }

    public double getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(double lon) {
        this.currentLong = lon;
    }

    public double getTargetLat() {
        return targetLat;
    }

    public void setTargetLat(double lat) {
        this.targetLat = lat;
    }

    public double getTargetLong() {
        return targetLong;
    }

    public void setTargetLong(double lon) {
        this.targetLong = lon;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(int bear) {
        this.bearing = bear;
    }
}
