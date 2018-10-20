package thanhnguyen.com.familysharinglocation;

/**
 * Created by THANHNGUYEN on 11/12/17.
 */

public class WeatherClassMarkerHelper {
    String username;
    double latitude;
    double longtitude;
    String lastupdate;
    String userid;

    public WeatherClassMarkerHelper(String username, double latitude, double longtitude, String lastupdate, String userid) {
        this.username = username;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.lastupdate=lastupdate;
        this.userid= userid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
