package ir.payebash.models.googlePlus;

import java.io.Serializable;

public class Plus_FullName_Item implements Serializable {

    public static final long serialVersionUID = 1L;
    public String familyName = "";
    public String givenName = "";

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }
}
