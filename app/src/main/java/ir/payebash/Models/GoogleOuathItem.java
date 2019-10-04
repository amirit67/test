package ir.payebash.Models;

import java.io.Serializable;

public class GoogleOuathItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String email = "";
    public String given_name = "";
    public String family_name = "";
    public String picture = "";
    public String name = "";
    public boolean email_verified = false;

    public String getEmail() {
        return email;
    }

    public String getGiven_name() {
        return given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public String getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public boolean getEmail_verified() {
        return email_verified;
    }

}
