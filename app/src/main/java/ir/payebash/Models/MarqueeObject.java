package ir.payebash.Models;

import com.google.gson.annotations.SerializedName;

public class MarqueeObject {

    @SerializedName("Name")
    private String Name;

    @SerializedName("Number")
    private String Number;

    @SerializedName("StatusCode")
    private boolean statusCode;

    public MarqueeObject(String name, String number, boolean statusCode) {
        Name = name;
        Number = number;
        this.statusCode = statusCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public boolean isStatusCode() {
        return statusCode;
    }

    public void setStatusCode(boolean statusCode) {
        this.statusCode = statusCode;
    }
}
