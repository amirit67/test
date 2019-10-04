package ir.payebash.Models;

import java.io.Serializable;

public class FilterFeedItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String Id = "";
    private String Name = "";
    private String hasChild = "";

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getHasChild() {
        return hasChild;
    }

    public void setHasChild(String hasChild) {
        this.hasChild = hasChild;
    }
}
