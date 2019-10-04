package ir.payebash.Models;

import java.io.Serializable;

public class NotifItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String Id = "";
    public String ParentId = "";
    public String HasChild = "";
    public String Name = "";
    public String Description = "";
    public String ImageUrl = "";
    public String AddedFeild = "";
    public String IsCommercial = "";
    public String Sort = "";
    public String State = "";

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getParentId() {
        return ParentId;
    }

    public String getHasChild() {
        return HasChild;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getIsCommercial() {
        return IsCommercial;
    }

    public String getAddedFeild() {
        return AddedFeild;
    }

    public String getSort() {
        return Sort;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}
