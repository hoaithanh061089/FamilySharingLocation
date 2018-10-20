package thanhnguyen.com.familysharinglocation;

import java.io.Serializable;

/**
 * Created by THANHNGUYEN on 11/24/17.
 */

public class PlacesClassHelper implements Serializable {

    String name;
    Double latitude;
    Double longtiude;
    String address;

    public PlacesClassHelper(String name, String address, Double latitude, Double longtiude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longtiude = longtiude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtiude() {
        return longtiude;
    }

    public void setLongtiude(Double longtiude) {
        this.longtiude = longtiude;
    }
}
