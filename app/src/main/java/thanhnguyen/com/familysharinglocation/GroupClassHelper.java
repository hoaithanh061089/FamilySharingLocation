package thanhnguyen.com.familysharinglocation;

import java.io.Serializable;

/**
 * Created by THANHNGUYEN on 11/24/17.
 */

public class GroupClassHelper implements Serializable {

    String name;
    String id;

    public GroupClassHelper(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
