package thanhnguyen.com.familysharinglocation;

/**
 * Created by THANHNGUYEN on 10/30/17.
 */

public class LocationHistoryClassHelper {
    String time;
    Double latitude;
    Double longtitude;
    int speed;
    String date;

    public LocationHistoryClassHelper(String date, String time, Double latitude, Double longtitude, int speed) {
        this.time = time;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.date=date;
        this.speed= speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }
}
