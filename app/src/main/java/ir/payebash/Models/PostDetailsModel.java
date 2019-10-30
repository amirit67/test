package ir.payebash.Models;

import java.io.Serializable;

public class PostDetailsModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String Id = "";
    private String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return this.Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }


}
