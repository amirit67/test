package ir.payebash.Models.googlePlus;

import java.io.Serializable;

public class Plus_Organizations_Item implements Serializable {

    public static final long serialVersionUID = 1L;
    public String name = "";
    public String title = "";
    public String type = "";
    public String startDate = "";
    public String endDate = "";

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

}
