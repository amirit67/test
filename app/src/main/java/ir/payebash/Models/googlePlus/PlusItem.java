package ir.payebash.Models.googlePlus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlusItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String occupation = "";
    public String displayName = "";
    public String gender = "";
    public Plus_FullName_Item name;
    public String tagline = "";
    public String aboutMe = "";
    public String relationshipStatus = "";
    public Plus_Image_Item image;
    public List<Plus_Organizations_Item> organizations = new ArrayList<>();
    public List<Plus_Place_Item> placesLived = new ArrayList<>();
    public List<Plus_Email_Item> emails = new ArrayList<>();
    public List<Plus_Birthday_Item> birthdays = new ArrayList<>();
    public boolean isPlusUser;

    public String getOccupation() {
        return occupation;
    }

    public String getdisplayName() {
        return displayName;
    }

    public List<Plus_Birthday_Item> getBirthdays() {
        return birthdays;
    }

    public String getGender() {
        return gender;
    }

    public String getTagline() {
        return tagline;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public Plus_FullName_Item getDisplayName() {
        return name;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public Plus_Image_Item getImage() {
        return image;
    }

    public List<Plus_Organizations_Item> getOrganizations() {
        return organizations;
    }

    public List<Plus_Place_Item> getPlacesLived() {
        return placesLived;
    }

    public List<Plus_Email_Item> getEmails() {
        return emails;
    }

    public boolean isPlusUser() {
        return isPlusUser;
    }
}
