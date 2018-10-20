package thanhnguyen.com.familysharinglocation;

/**
 * Created by THANHNGUYEN on 10/22/17.
 */
public class UserInfo  {

    String username, usermobilenumber;


    public UserInfo(String username, String usermobilenumber) {
        this.username = username;
        this.usermobilenumber = usermobilenumber;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsermobilenumber() {
        return usermobilenumber;
    }

    public void setUsermobilenumber(String usermobilenumber) {
        this.usermobilenumber = usermobilenumber;
    }
}


